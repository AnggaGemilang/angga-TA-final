#include "RTClib.h"
#include "string.h"
#include <LiquidCrystal_I2C.h>
#include <TaskScheduler.h>
#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include <HTTPClient.h>
#include "StringSplitter.h"
#include <Time.h>

#define RXp2 16
#define TXp2 17
#define FERTILIZER_TRIG_PIN 5
#define FERTILIZER_ECHO_PIN 18
#define WATER_TRIG_PIN 33
#define WATER_ECHO_PIN 32
#define SOUND_SPEED 0.034
#define TANK_SURFACE_AREA 81
#define TANK_HEIGHT 26
#define TANK_MAX_VOLUME 2106
#define LCD_COLUMNS 16
#define LCD_ROWS 2
#define PUMP_RELAY_1 14
#define PUMP_RELAY_2 15
#define SECS_PER_DAY 86400
#define DATABASE_URL "https://fertigation-system-389e8-default-rtdb.firebaseio.com/"
#define API_KEY "AIzaSyBZvwV5-74YkBUlphAYpuyFsHIQVyfRHW4"
#define FCM_URL "https://fcm.googleapis.com/fcm/send"
#define FCM_API_KEY "key=AAAAx7B7jBc:APA91bEL6FTL_bKgKLOFIteAL7c9iXI54Le2-D7tegps_shgzI-5c5Mqtblou5bPpQGayfYJrxhLcmrF8rZe5LqMv5rnbb2SKd71BvbStSNaaS9vfW6T1rItbIZEMtHObvAbHF55aF4X"
#define DEVICE_FCM_KEY "e0tQlw-Az2A:APA91bHtEdYptmOYWWCzEWUepfhGyq10VONGUl7ToUf91-TxWRlVUEM2ClsgE2P9GmTVGVLrnlDMNx5WY-0U4MYw7gqCr9f2MKOTWYltqC34jB8LzFd8-Pl54xwVEdxx-vCDWoyl7LPq"
#define WIFI_SSID "SPEEDY"
#define WIFI_PASSWORD "suherman"
// #define WIFI_SSID "Galaxy M33 5G"
// #define WIFI_PASSWORD "anggaganteng"

// RTC_DS3231 rtc;
LiquidCrystal_I2C lcd_i2c(0x27, 16, 2);  
Scheduler userScheduler;
HTTPClient HttpClient;
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

String monitorDeviceData, irrigationTimes="10:57", fertigationTimes, fertigationStatus = "off", r_time;
String irrigationDoses = "750", fertigationDoses = "500", irrigationAge, fertigationAge, initialPlantPlanting = "1,12,2023,1,20,00";
int fertilizerTankVal, waterTankVal, idealMoisture, plantAgeNow, initialPlantAge = 2;
int irrigationDays = 2, fertigationDays, userInterval, fertigationDose;
int irrigationDuration, fertigationDuration, irrigationDose, moistureVal,waterLevelVal;
unsigned long lastIrrigation, lastFertigation, lastDayIrrigation, lastDayFertigation;
bool irrigationStatus = false, autoIrrigationStatus = false;
String BASE_URL_MONITORING = "/monitoring/2X0JeST2hUe8K5qLpk5f6gCC0zO2/13kjh123kj1h3j12h21312kjhasdasd/primaryDevice/";
String BASE_URL_CONTROLLING = "/controlling/2X0JeST2hUe8K5qLpk5f6gCC0zO2/parameter/13kjh123kj1h3j12h21312kjhasdasd/";
String BASE_URL_MONITORING_2 = "/monitoring/2X0JeST2hUe8K5qLpk5f6gCC0zO2/13kjh123kj1h3j12h21312kjhasdasd/monitorDevice/1/";

int waterTank();
int fertilizerTank();
void sendMessage();
void readControlData();

Task taskSendMessage( userInterval , TASK_FOREVER, &sendMessage );
Task taskReadControlData( TASK_SECOND * 18 , TASK_FOREVER, &readControlData );

void sendMessage() {
  fertilizerTankVal = fertilizerTank();
  waterTankVal =  waterTank();
  Firebase.RTDB.setInt(&fbdo, BASE_URL_MONITORING + "fertilizerTank", fertilizerTankVal);
  if(Firebase.RTDB.setInt(&fbdo, BASE_URL_MONITORING + "waterTank", waterTankVal)){
    Serial.printf("Upload to primary device msgFTank=%d msgFTank=%d\n", fertilizerTankVal, waterTankVal);
  } else {
    Serial.println(fbdo.errorReason());
  }
  taskSendMessage.setInterval(userInterval);
}

void readControlData(){
  if(Firebase.RTDB.getInt(&fbdo, BASE_URL_CONTROLLING + "idealMoisture")){
    if(fbdo.dataType() == "int"){
      idealMoisture = fbdo.intData();
    }
  }

  if(Firebase.RTDB.getInt(&fbdo, BASE_URL_CONTROLLING + "irrigationDays")){
    if(fbdo.dataType() == "int"){
      irrigationDays = fbdo.intData();
    }
  }    

  if(Firebase.RTDB.getInt(&fbdo, BASE_URL_CONTROLLING + "fertigationDays")){
    if(fbdo.dataType() == "int"){
      fertigationDays = fbdo.intData();
    }
  }    

  if(Firebase.RTDB.getString(&fbdo, BASE_URL_CONTROLLING + "irrigationTimes")){
    if(fbdo.dataType() == "string"){
      irrigationTimes = fbdo.stringData();
    }
  }    

  if(Firebase.RTDB.getString(&fbdo, BASE_URL_CONTROLLING + "fertigationTimes")){
    if(fbdo.dataType() == "string"){
      fertigationTimes = fbdo.stringData();
    }
  }    
  
  if(Firebase.RTDB.getString(&fbdo, BASE_URL_CONTROLLING + "irrigationDoses")){
    if(fbdo.dataType() == "string"){
      irrigationDoses = fbdo.stringData();
    }
  }    

  if(Firebase.RTDB.getString(&fbdo, BASE_URL_CONTROLLING + "fertigationDoses")){
    if(fbdo.dataType() == "string"){
      fertigationDoses = fbdo.stringData();
    }
  }

  if(Firebase.RTDB.getInt(&fbdo, BASE_URL_CONTROLLING + "irrigationAge")){
    if(fbdo.dataType() == "string"){
      irrigationAge = fbdo.stringData();
      StringSplitter *irrigationAgeSplitter = new StringSplitter(irrigationAge, ',', 3);
      int itemCount2 = irrigationAgeSplitter->getItemCount();
      for(int i = itemCount2; i >= 1; i--){
        if(plantAgeNow >= irrigationAgeSplitter->getItemAtIndex(i).toInt()){
          StringSplitter *irrigationDosesSplitter = new StringSplitter(irrigationDoses, ',', 3);
          irrigationDose = irrigationDosesSplitter->getItemAtIndex(i).toInt();
        }
      }
    }
  }

  if(Firebase.RTDB.getInt(&fbdo, BASE_URL_CONTROLLING + "fertigationAge")){
    if(fbdo.dataType() == "string"){
      fertigationAge = fbdo.stringData();
      StringSplitter *fertigationAgeSplitter = new StringSplitter(fertigationAge, ',', 3);
      int itemCount2 = fertigationAgeSplitter->getItemCount();
      for(int i = itemCount2; i >= 1; i--){
        if(plantAgeNow >= fertigationAgeSplitter->getItemAtIndex(i).toInt()){
          StringSplitter *fertigationDosesSplitter = new StringSplitter(fertigationDoses, ',', 3);
          fertigationDose = fertigationDosesSplitter->getItemAtIndex(i).toInt();
        }
      }      
    }
  }

  if(Firebase.RTDB.getInt(&fbdo, "/controlling/2X0JeST2hUe8K5qLpk5f6gCC0zO2/interval/userRequest")){
    if(fbdo.dataType() == "int"){
      userInterval = fbdo.intData();
    }
  }

  if(Firebase.RTDB.getInt(&fbdo, BASE_URL_MONITORING_2 + "moisture")){
    if(fbdo.dataType() == "int"){
      moistureVal = fbdo.intData();
    }
  }  

  if(Firebase.RTDB.getInt(&fbdo, BASE_URL_MONITORING_2 + "waterLevel")){
    if(fbdo.dataType() == "int"){
      waterLevelVal = fbdo.intData();
    }
  }    

  Serial.printf("Get data Controlling mois=%d irrDays=%d FerDays=%d IrrTimes=%s FerTimes=%s IrrDoses=%s FerDoses=%s UsrInt=%d\n", idealMoisture, irrigationDays, fertigationDays, irrigationTimes, fertigationTimes, irrigationDoses, fertigationDoses, userInterval);
  Serial.printf("Get data Monitor Device mois=%d waterLevel=%d\n", moistureVal, waterLevelVal);
  taskReadControlData.setInterval((TASK_SECOND * 18));    
}

void sendNotification(String title, String body) {
  if ((WiFi.status() == WL_CONNECTED)) {  
    HttpClient.begin(String(FCM_URL));
    HttpClient.addHeader("Authorization", String(FCM_API_KEY));
    HttpClient.addHeader("Content-Type", "application/json");
    String data = "{\"registration_ids\": [\"" + String(DEVICE_FCM_KEY) + "\"], \"notification\": {\"body\":\"" + body + "\", \"title\":\"" + title + "\"}}";
    int httpCode = HttpClient.POST(data);
    if (httpCode == HTTP_CODE_OK) {  
      Serial.println("Notification Sent To The Phone");
    } else {
      Serial.println("Error on sending notification");
      Serial.println(httpCode);
      Serial.println(HttpClient.getString());
    }
    HttpClient.end();
  }
}

int getDifferenceDays(tmElements_t startDate, tmElements_t endDate) {
  time_t started = makeTime(startDate);
  time_t ended = makeTime(endDate);
  time_t differenceSeconds = ended - started;
  int differenceDays = differenceSeconds / SECS_PER_DAY;
  return differenceDays;
}

void setup() {
  Serial.begin(115200);

  lcd_i2c.init();
  lcd_i2c.backlight();
  
  pinMode(FERTILIZER_TRIG_PIN, OUTPUT);
  pinMode(FERTILIZER_ECHO_PIN, INPUT);
  pinMode(WATER_TRIG_PIN, OUTPUT);
  pinMode(WATER_ECHO_PIN, INPUT);
  pinMode(PUMP_RELAY_1, OUTPUT);
  pinMode(PUMP_RELAY_2, OUTPUT);

  lcd_i2c.clear();
  lcd_i2c.setCursor(3, 0);
  lcd_i2c.print("Welcome To");
  lcd_i2c.setCursor(0, 1);
  lcd_i2c.print("*** KoTA 203 ***");
  delay(2000);

//  if (!rtc.begin()) {
//    Serial.println("Couldn't find RTC");
//    while(1);
//  }

//  rtc.adjust(DateTime(__DATE__, __TIME__));

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);                                  
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID);
  while (WiFi.status() != WL_CONNECTED) 
  {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;

  if (Firebase.signUp(&config, &auth, "", "")){
    Serial.println("Firebase Connected");
  }
  else{
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }
  
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  if(Firebase.RTDB.getInt(&fbdo, BASE_URL_CONTROLLING + "initialPlantAge")){
    if(fbdo.dataType() == "int"){
      initialPlantAge = fbdo.intData();
      StringSplitter *plantPlantingSplitter = new StringSplitter(initialPlantPlanting, ',', 6);
      tmElements_t tanggalAwal;
      tanggalAwal.Second = plantPlantingSplitter->getItemAtIndex(5).toInt();
      tanggalAwal.Minute = plantPlantingSplitter->getItemAtIndex(4).toInt();
      tanggalAwal.Hour = plantPlantingSplitter->getItemAtIndex(3).toInt();
      tanggalAwal.Day = plantPlantingSplitter->getItemAtIndex(0).toInt();
      tanggalAwal.Month = plantPlantingSplitter->getItemAtIndex(1).toInt();
      tanggalAwal.Year = plantPlantingSplitter->getItemAtIndex(2).toInt() - 1970;
      
      tmElements_t tanggalAkhir;
      tanggalAkhir.Second = 45;
      tanggalAkhir.Minute = 30;
      tanggalAkhir.Hour = 12;
      tanggalAkhir.Day = 5;
      tanggalAkhir.Month = 1;
      tanggalAkhir.Year = 2023 - 1970; 
            
      int perbedaan = getDifferenceDays(tanggalAwal, tanggalAkhir);
      plantAgeNow = initialPlantAge + perbedaan;
    }
  }

  if(Firebase.RTDB.getInt(&fbdo, BASE_URL_CONTROLLING + "userRequest")){
    if(fbdo.dataType() == "int"){
      userInterval = fbdo.intData();
    }
  }

  userScheduler.init();
  userScheduler.addTask(taskReadControlData);
  userScheduler.addTask(taskSendMessage);
  taskReadControlData.enable();  
  taskSendMessage.enable();
}

void loop() {
  userScheduler.execute();
  
  //  lcd_i2c.clear();
  //  lcd_i2c.setCursor(0, 0);
  //  lcd_i2c.print("Moisture(1): 20%");
  //  lcd_i2c.setCursor(0, 1);
  //  lcd_i2c.print("Water(2): 20 cm");

  //  lcd_i2c.clear();
  //  lcd_i2c.setCursor(0, 0);
  //  lcd_i2c.print("Moisture(2): 20%");
  //  lcd_i2c.setCursor(0, 1);
  //  lcd_i2c.print("Water(2): 20 cm");

  //  lcd_i2c.clear();
  //  lcd_i2c.setCursor(0, 0);
  //  lcd_i2c.print("Water: 20");
  //  lcd_i2c.setCursor(0, 1);
  //  lcd_i2c.print("Fertilizer: 20%");
  
  // DateTime now = rtc.now();

  if(autoIrrigationStatus == false && irrigationStatus == false && fertigationStatus == "off"){
    if((double)moistureVal <= (double)(0.3*idealMoisture)){
        Serial.println("Nyala1");
        // Nyalain pompa sama solenoid valve
        autoIrrigationStatus = true;
        delay(10000);
        if(waterLevelVal < 700){
          sendNotification("air gk sampe", "segera cek kemungkinan kerusakan pada pompa");
        }
    }
  }

  if(irrigationStatus == false && autoIrrigationStatus == false && fertigationStatus == "off"){
    if(fertigationDays > 1){
      if(lastDayFertigation == 0){
        StringSplitter *fertigationTimesSplitter = new StringSplitter(fertigationTimes, ',', 3);
        int itemCount = fertigationTimesSplitter->getItemCount();
        for(int i = 0; i < itemCount; i++){
          String item = fertigationTimesSplitter->getItemAtIndex(i);
          if(item == "10:57"){           
            // digitalWrite(PUMP_RELAY_1, HIGH);
            // Nyalain pompa sama solenoid valve
            fertigationStatus = "irrigation1";
            lastFertigation = millis();
            if(i == 0){
              lastDayFertigation = millis();
            } 
            delay(10000);
            if(waterLevelVal < 700){
              sendNotification("air gk sampe", "segera cek kemungkinan kerusakan pada pompa");
            }
          }
        }
      } else {
        unsigned long elapsedTime = millis() - lastDayFertigation;
//        unsigned long dayCounter = elapsedTime / (1000UL * 60UL * 60UL * 24UL);
//        if(dayCounter >= lastFertigation){
        if (elapsedTime >= 120000UL) {
          StringSplitter *fertigationTimesSplitter = new StringSplitter(fertigationTimes, ',', 3);
          int itemCount = fertigationTimesSplitter->getItemCount();
          for(int i = 0; i < itemCount; i++){
            String item = fertigationTimesSplitter->getItemAtIndex(i);
            if(item == "10:57"){
              // Nyalain pompa sama solenoid valve
              fertigationStatus = true;
              lastFertigation = millis();
              if(i == 0){
                lastDayFertigation = millis();
              }
              delay(10000);
              if(waterLevelVal < 700){
                sendNotification("air gk sampe", "segera cek kemungkinan kerusakan pada pompa");
              }
            }
          }
        }
      }
    } else {
      StringSplitter *fertigationTimesSplitter = new StringSplitter(fertigationTimes, ',', 3);
      int itemCount = fertigationTimesSplitter->getItemCount();
      for(int i = 0; i < itemCount; i++){
        String item = fertigationTimesSplitter->getItemAtIndex(i);
        if(item == "10:57"){           
          Serial.println("Nyala3");
          // Nyalain pompa sama solenoid valve
          fertigationStatus = "irrigation1";
          lastFertigation = millis();
          delay(10000);
          if(waterLevelVal < 700){
            sendNotification("air gk sampe", "segera cek kemungkinan kerusakan pada pompa");
          }
        }
      }
    }    
  }

  if(irrigationStatus == false && autoIrrigationStatus == false && fertigationStatus == "off"){
    if(irrigationDays > 1){
      if(lastDayIrrigation == 0){
        StringSplitter *irrigationTimesSplitter = new StringSplitter(irrigationTimes, ',', 3);
        int itemCount = irrigationTimesSplitter->getItemCount();
        for(int i = 0; i < itemCount; i++){
          String item = irrigationTimesSplitter->getItemAtIndex(i);
          if(item == "10:57"){
            if(moistureVal > idealMoisture){
              // Nyalain pompa sama solenoid valve
              irrigationStatus = true;
              lastIrrigation = millis();
              if(i == 0){
                lastDayIrrigation = millis();
              } 
              delay(10000);
              if(waterLevelVal < 700){
                sendNotification("air gk sampe", "segera cek kemungkinan kerusakan pada pompa");
              }
            } else {
              sendNotification("Kelembaban berlebih", "Kegiatan penyiraman dilewati");
            }
          }
        }
      } else {
        unsigned long elapsedTime = millis() - lastDayIrrigation;
//        unsigned long dayCounter = elapsedTime / (1000UL * 60UL * 60UL * 24UL);
//        if(dayCounter >= lastIrrigation){
        if (elapsedTime >= 120000UL) {
          StringSplitter *irrigationTimesSplitter = new StringSplitter(irrigationTimes, ',', 3);
          int itemCount = irrigationTimesSplitter->getItemCount();
          for(int i = 0; i < itemCount; i++){
            String item = irrigationTimesSplitter->getItemAtIndex(i);
            if(item == "10:57"){            
              // Nyalain pompa sama solenoid valve
              irrigationStatus = true;
              lastIrrigation = millis();
              if(i == 0){
                lastDayIrrigation = millis();
              }
              delay(10000);
              if(waterLevelVal < 700){
                sendNotification("air gk sampe", "segera cek kemungkinan kerusakan pada pompa");
              }
            }
          }
        }
      }
    } else {
      StringSplitter *irrigationTimesSplitter = new StringSplitter(irrigationTimes, ',', 3);
      int itemCount = irrigationTimesSplitter->getItemCount();
      for(int i = 0; i < itemCount; i++){
        String item = irrigationTimesSplitter->getItemAtIndex(i);
        if(item == "10:57"){          
          // Nyalain pompa sama solenoid valve
          irrigationStatus = true;
          lastIrrigation = millis();
          delay(10000);
          if(waterLevelVal < 700){
            sendNotification("air gk sampe", "segera cek kemungkinan kerusakan pada pompa");
          }          
        }
      }
    }    
  }

  if(autoIrrigationStatus == true && irrigationStatus == false && fertigationStatus == "off"){
    if(moistureVal >= idealMoisture){
      irrigationStatus = false;
      // Matiin pompa sama solenoid valve      
    }
  }

  if(irrigationStatus == true && autoIrrigationStatus == false && fertigationStatus == "off"){
    // if(millis() >= (lastIrrigation+(irrigationDuration * 1000))){
    if(millis() >= (lastIrrigation+120000UL)){
      Serial.println("Mati");
      irrigationStatus = false;
      // Matiin pompa sama solenoid valve
    }
  }

  if(fertigationStatus == "irrigation1" && autoIrrigationStatus == false && irrigationStatus == false){
    // if(millis() >= (lastFertigation+(irrigationDuration * 1000))){
    if(millis() >= (lastFertigation+120000UL)){
      fertigationStatus = "fertigation";
      lastFertigation = millis();
      // Matiin pompa sama solenoid valve
    }
  }

  if(fertigationStatus == "fertigation" && autoIrrigationStatus == false && irrigationStatus == false){
    // if(millis() >= (lastFertigation+(fertigationDuration * 1000))){
    if(millis() >= (lastFertigation+120000UL)){
      Serial.println("Mati");
      fertigationStatus = "irrigation2";
      lastFertigation = millis();
      // Matiin solenoid valve air dan nyalain solenoid valve pupuk
    }
  }

  if(fertigationStatus == "irrigation2" && autoIrrigationStatus == false && irrigationStatus == false){
    // if(millis() >= (lastFertigation+(irrigationDuration * 1000))){
    if(millis() >= (lastFertigation+120000UL)){
      Serial.println("Mati");
      fertigationStatus = "irrigation2";
      lastFertigation = millis();
      // Matiin pompa sama solenoid valve
    }
  }

  time_t t = now();
  int timeFromLib = hour(t)*60 + minute(t);
  StringSplitter *RTCtime = new StringSplitter(r_time, ',', 2);
  int timeFromRTC = RTCtime->getItemAtIndex(0).toInt() * 60 + RTCtime->getItemAtIndex(1).toInt();
  if(timeFromRTC - timeFromLib >= 60){
    sendNotification("Waktu Telat", "segera cek kemungkinan kerusakan pada sensor RTC");
  }
  
  delay(2000);
}