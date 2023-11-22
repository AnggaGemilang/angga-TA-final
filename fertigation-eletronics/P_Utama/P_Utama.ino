#include "RTClib.h"
#include "string.h"
#include <LiquidCrystal_I2C.h>
#include <ArduinoJson.h>
#include <TaskScheduler.h>
#include <WiFi.h>
#include <HardwareSerial.h>

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
#define HARDWARE_CODE "13kjh123kj1h3j12h21312kjhasdasd"
#define OWNER_CODE "hpoQA4Xv0hTpmsB3lgOXyrRF7S12"
#define FIREBASE_HOST "https://fertigation-system-389e8-default-rtdb.firebaseio.com/"
#define FIREBASE_AUTH "AIzaSyBZvwV5-74YkBUlphAYpuyFsHIQVyfRHW4"
#define WIFI_SSID "SPEEDY"
#define WIFI_PASSWORD "suherman"
// #define WIFI_SSID "Galaxy M33 5G"
// #define WIFI_PASSWORD "anggaganteng"

// RTC_DS3231 rtc;
LiquidCrystal_I2C lcd_i2c(0x27, 16, 2);  
HardwareSerial SerialPort(2);
Scheduler userScheduler;

String monitorDeviceData, irrigationTimes, fertigationTimes;
int fertilizerTankVal, waterTankVal, idealMoisture;
int irrigationDays, fertigationDays;
int irrigationDose, fertigationDose;
int systemInterval, userInterval;

String timeNow();
int waterTank();
int fertilizerTank();
void sendMessage();
void readControlData();

// Task taskReadControlData( TASK_SECOND * 18 , TASK_FOREVER, &readControlData );

void sendMessage() {
  Serial.println(monitorDeviceData);
}

// void readControlData(){
//    idealMoisture = Firebase.getInt(fbdo, "/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/idealMoisture");
//    irrigationDays = Firebase.getInt(fbdo, "/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/irrigationDays");
//    fertigationDays = Firebase.getInt(fbdo, "/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/fertigationDays");
//    irrigationTimes = Firebase.getString(fbdo, "/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/irrigationTimes");            
//    fertigationTimes = Firebase.getString(fbdo, "/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/fertigationTimes");
//    irrigationDose = Firebase.getInt(fbdo, "/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/irrigationDose");
//    fertigationDose = Firebase.getInt(fbdo, "/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/fertigationDose");
//    systemInterval = Firebase.getInt(fbdo, "/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/interval/systemRequest");
//    userInterval = Firebase.getInt(fbdo, "/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/interval/userRequest");        
//    Serial.printf("Get data mois=%d irrDays=%d FerDays=%d IrrTimes=%s FerTimes=%s IrrDose=%d FerDose=%d SysInt=%d UsrInt=%d\n", idealMoisture, irrigationDays, fertigationDays, irrigationTimes, fertigationTimes, irrigationDose, fertigationDose, systemInterval, userInterval);
//    Serial.printf("Get data mois=%d\n", idealMoisture);
    
//    taskReadControlData.setInterval((TASK_SECOND * 13));    
// }

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

  userScheduler.init();
//  userScheduler.addTask( taskReadControlData );
//  taskReadControlData.enable();  
}

void loop() {

  userScheduler.execute();

  if (SerialPort.available())
  {
    monitorDeviceData = String(SerialPort.readString());
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
