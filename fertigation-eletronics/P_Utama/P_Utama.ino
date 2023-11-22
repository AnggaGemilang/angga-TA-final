#include "RTClib.h"
#include "string.h"
#include <LiquidCrystal_I2C.h>
#include <ArduinoJson.h>
#include <TaskScheduler.h>
#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include <HardwareSerial.h>
#include <HTTPClient.h>

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
#define PUMP_RELAY 13
#define VALVE_RELAY_1 14
#define VALVE_RELAY_2 15
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
HardwareSerial SerialPort(2);
Scheduler userScheduler;
HTTPClient HttpClient;
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

String monitorDeviceData, irrigationTimes, fertigationTimes, tempLastIrrigation, tempatLastFertigation;
int fertilizerTankVal, waterTankVal, idealMoisture;
int irrigationDays, fertigationDays;
int irrigationDose, fertigationDose, userInterval;

String timeNow();
int waterTank();
int fertilizerTank();
void sendMessage();
void readControlData();

Task taskReadControlData( TASK_SECOND * 18 , TASK_FOREVER, &readControlData );

void sendMessage() {
  if(monitorDeviceData.length() > 6){
    StaticJsonDocument<300> doc;
    deserializeJson(doc, monitorDeviceData);
    String source = doc["source"];
    int moisture = doc["moisture"];
    int waterLevel = doc["water_level"];
    if(source == "PP_1"){
      Firebase.RTDB.setInt(&fbdo, "/monitoring/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/13kjh123kj1h3j12h21312kjhasdasd/monitorDevice1/moisture", moisture);
      Firebase.RTDB.setInt(&fbdo, "/monitoring/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/13kjh123kj1h3j12h21312kjhasdasd/monitorDevice1/water_level", waterLevel);
    } else if (source == "PP_2"){
      Firebase.RTDB.setInt(&fbdo, "/monitoring/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/13kjh123kj1h3j12h21312kjhasdasd/monitorDevice2/moisture", moisture);
      Firebase.RTDB.setInt(&fbdo, "/monitoring/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/13kjh123kj1h3j12h21312kjhasdasd/monitorDevice2/water_level", waterLevel);
    }
    fertilizerTankVal = fertilizerTank();
    waterTankVal =  waterTank();
    Firebase.RTDB.setInt(&fbdo, "/monitoring/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/13kjh123kj1h3j12h21312kjhasdasd/primaryDevice/fertilizerTank", fertilizerTankVal);
    if(Firebase.RTDB.setInt(&fbdo, "/monitoring/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/13kjh123kj1h3j12h21312kjhasdasd/primaryDevice/waterTank", waterTankVal)){
      Serial.printf("Upload to monitor device msgMois=%d msgWater=%d\n", moisture, waterLevel);
      Serial.printf("Upload to primary device msgFTank=%d msgFTank=%d\n", fertilizerTankVal, waterTankVal);
    } else {
      Serial.println(fbdo.errorReason());
    }
  }
}

void readControlData(){
  if(Firebase.RTDB.getInt(&fbdo, "/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/idealMoisture")){
    if(fbdo.dataType() == "int"){
      idealMoisture = fbdo.intData();
    }
  }

  if(Firebase.RTDB.getInt(&fbdo, "/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/irrigationDays")){
    if(fbdo.dataType() == "int"){
      irrigationDays = fbdo.intData();
    }
  }    

  if(Firebase.RTDB.getInt(&fbdo, "/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/fertigationDays")){
    if(fbdo.dataType() == "int"){
      fertigationDays = fbdo.intData();
    }
  }    

  if(Firebase.RTDB.getString(&fbdo, "/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/irrigationTimes")){
    if(fbdo.dataType() == "string"){
      irrigationTimes = fbdo.stringData();
    }
  }    

  if(Firebase.RTDB.getInt(&fbdo, "/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/fertigationTimes")){
    if(fbdo.dataType() == "string"){
      fertigationTimes = fbdo.stringData();
    }
  }    
  
  if(Firebase.RTDB.getInt(&fbdo, "/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/irrigationDose")){
    if(fbdo.dataType() == "int"){
      irrigationDose = fbdo.intData();
    }
  }    

  if(Firebase.RTDB.getInt(&fbdo, "/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/fertigationDose")){
    if(fbdo.dataType() == "int"){
      fertigationDose = fbdo.intData();
    }
  }

  if(Firebase.RTDB.getInt(&fbdo, "/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/userRequest")){
    if(fbdo.dataType() == "int"){
      userInterval = fbdo.intData();
    }
  }
  Serial.printf("Get data mois=%d irrDays=%d FerDays=%d IrrTimes=%s FerTimes=%s IrrDose=%d FerDose=%d UsrInt=%d\n", idealMoisture, irrigationDays, fertigationDays, irrigationTimes, fertigationTimes, irrigationDose, fertigationDose, userInterval);
  taskReadControlData.setInterval((TASK_SECOND * 18));    
}

void sendNotification(String title, String body) {
  if ((WiFi.status() == WL_CONNECTED)) {  
    //Specify the URL
    HttpClient.begin(String(FCM_URL));

    // Set headers
    HttpClient.addHeader("Authorization", String(FCM_API_KEY));
    HttpClient.addHeader("Content-Type", "application/json");

    // Data string
    String data = "{\"registration_ids\": [\"" + String(DEVICE_FCM_KEY) + "\"], \"notification\": {\"body\":\"" + body + "\", \"title\":\"" + title + "\"}}";

    //Make the request
    int httpCode = HttpClient.POST(data);

    //Check for the returning code
    if (httpCode == HTTP_CODE_OK) {  
      Serial.println("Notification Sent To The Phone");
    } else {
      Serial.println("Error on sending notification");
      Serial.println(httpCode);
      Serial.println(HttpClient.getString());
    }
    HttpClient.end();  //Free the resources
  }
}

void setup() {
  Serial.begin(115200);
  SerialPort.begin(9600, SERIAL_8N1, 16, 17);

  lcd_i2c.init();
  lcd_i2c.backlight();
  
  pinMode(FERTILIZER_TRIG_PIN, OUTPUT);
  pinMode(FERTILIZER_ECHO_PIN, INPUT);
  pinMode(PUMP_RELAY, OUTPUT);
  pinMode(WATER_TRIG_PIN, OUTPUT);
  pinMode(WATER_ECHO_PIN, INPUT);

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

  userScheduler.init();
  userScheduler.addTask( taskReadControlData );
  taskReadControlData.enable();  
}

void loop() {
  userScheduler.execute();

  if (SerialPort.available())
  {
    monitorDeviceData = String(SerialPort.readString());
    Serial.println("monitorDeviceData: " + monitorDeviceData);
    sendMessage();
  }
  
  //  digitalWrite(PUMP_RELAY, HIGH);
  
  //  lcd_i2c.clear();
  //  lcd_i2c.setCursor(0, 0);
  //  lcd_i2c.print("Moisture(1): 20%");
  //  lcd_i2c.setCursor(0, 1);
  //  lcd_i2c.print("Water(2): 20 cm");

  //  digitalWrite(PUMP_RELAY, LOW);

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

  // Serial.print("Waktu: ");
  // Serial.println(timeNow());
}
