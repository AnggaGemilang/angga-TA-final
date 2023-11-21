#include "RTClib.h"
#include "string.h"
#include <LiquidCrystal_I2C.h>
#include <ArduinoJson.h>
#include <painlessMesh.h>
#include <FirebaseESP32.h>

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
#define MESH_PREFIX "fertigation-kota203"
#define MESH_PASSWORD "f3rt1g4t10n" 
#define MESH_PORT 5555

// RTC_DS3231 rtc;
LiquidCrystal_I2C lcd_i2c(0x27, 16, 2);  

Scheduler userScheduler;
painlessMesh  mesh;
FirebaseData fbdo;

String monitorDeviceData;
String primaryDeviceData;

String timeNow();
int waterTank();
int fertilizerTank();
void sendMessage();

Task taskSendMessage( TASK_SECOND * 11 , TASK_FOREVER, &sendMessage );

void sendMessage() {
  if(monitorDeviceData.length() > 6){
    StaticJsonDocument<300> doc;
    deserializeJson(doc, monitorDeviceData);
    String source = doc["source"];
    int moisture = doc["moisture"];
    int waterLevel = doc["water_level"];
    FirebaseJson updateData;
    updateData.set("moisture",moisture);
    updateData.set("water_level",waterLevel);
    if(source == "PP_1"){
      Firebase.setInt(fbdo,"/monitor/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/13kjh123kj1h3j12h21312kjhasdasd/pemantau_1/moisture", moisture);
      Firebase.setInt(fbdo,"/monitor/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/13kjh123kj1h3j12h21312kjhasdasd/pemantau_2/water_level", waterLevel);      
      Serial.printf("Upload to pemantau 1 msgMois=%d msgWater=%d\n", moisture, waterLevel);
    } else if (source == "PP_2"){
      Firebase.setInt(fbdo,"/monitor/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/13kjh123kj1h3j12h21312kjhasdasd/pemantau_2/moisture", moisture);
      Firebase.setInt(fbdo,"/monitor/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/13kjh123kj1h3j12h21312kjhasdasd/pemantau_2/water_level", waterLevel);
      Serial.printf("Upload to pemantau 2 msgMois=%d msgWater=%d\n", moisture, waterLevel);
    }
  }
  taskSendMessage.setInterval((TASK_SECOND * 11));      
}

void receivedCallback( uint32_t from, String &msg ) {
  if(msg.length() > 6){
    Serial.printf("startHere: Received from %u msg=%s\n", from, msg.c_str());    
    monitorDeviceData = msg.c_str();
  }
}

void newConnectionCallback(uint32_t nodeId) {
  Serial.printf("--> startHere: New Connection, nodeId = %u\n", nodeId);
}

void changedConnectionCallback() {
  Serial.printf("Changed connections\n");
}

void nodeTimeAdjustedCallback(int32_t offset) {
  Serial.printf("Adjusted time %u. Offset = %d\n", mesh.getNodeTime(),offset);
}

void setup() {
  Serial.begin(115200);
  lcd_i2c.init();
  lcd_i2c.backlight();
  
  mesh.setDebugMsgTypes( ERROR | STARTUP );
  mesh.init( MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT );
  mesh.onReceive(&receivedCallback);
  mesh.onNewConnection(&newConnectionCallback);
  mesh.onChangedConnections(&changedConnectionCallback);
  mesh.onNodeTimeAdjusted(&nodeTimeAdjustedCallback);
  mesh.stationManual(WIFI_SSID, WIFI_PASSWORD);
  mesh.setRoot(true);
  mesh.setContainsRoot(true);

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

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Serial.println("Firebase Connected");

  userScheduler.addTask( taskSendMessage );
  taskSendMessage.enable();
}

void loop() {
  mesh.update();

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
  
  // fertilizerTank();
  // waterTank();

  // DateTime now = rtc.now();

  // Serial.print("Waktu: ");
  // Serial.println(timeNow());
}
