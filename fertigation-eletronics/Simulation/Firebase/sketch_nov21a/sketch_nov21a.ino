#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include <TaskScheduler.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

#define WIFI_SSID "SPEEDY"
#define WIFI_PASSWORD "suherman"
#define HARDWARE_CODE "13kjh123kj1h3j12h21312kjhasdasd"
#define OWNER_CODE "hpoQA4Xv0hTpmsB3lgOXyrRF7S12"
#define DATABASE_URL "https://fertigation-system-389e8-default-rtdb.firebaseio.com/"
#define API_KEY "AIzaSyBZvwV5-74YkBUlphAYpuyFsHIQVyfRHW4"

unsigned long sendDataPrevMillis = 0;
int count = 0;
bool signupOK = false;

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
Scheduler userScheduler;

String monitorDeviceData, irrigationTimes, fertigationTimes;
int fertilizerTankVal, waterTankVal, idealMoisture;
int irrigationDays, fertigationDays;
int irrigationDose, fertigationDose;
int systemInterval, userInterval;

void readControlData();

Task taskReadControlData( TASK_SECOND * 5 , TASK_FOREVER, &readControlData );

void readControlData(){
  if(Firebase.RTDB.getInt(&fbdo,"/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/idealMoisture")){
    if(fbdo.dataType() == "int"){
      idealMoisture = fbdo.intData();
      Serial.printf("Get data mois=%d", idealMoisture);
    }
  }    
  taskReadControlData.setInterval((TASK_SECOND * 5));    
}

void setup() {
  Serial.begin(115200);  
  while (!Serial);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);                                  
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID);
  while (WiFi.status() != WL_CONNECTED) 
  {
    Serial.print(".");
    delay(500);
  }
 
  Serial.println();
  Serial.print("Connected");
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());
  
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;

  if (Firebase.signUp(&config, &auth, "", "")){
    Serial.println("Firebase Connected");
    signupOK = true;
  }
  else{
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }

  config.token_status_callback = tokenStatusCallback;
  
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  userScheduler.addTask(taskReadControlData);
  taskReadControlData.enable();
}


void loop() {
  userScheduler.execute();

  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 15000 || sendDataPrevMillis == 0)){
    sendDataPrevMillis = millis();
    // Write an Int number on the database path test/int
    if (Firebase.RTDB.setInt(&fbdo, "test/int", count)){
      Serial.println("PASSED");
      Serial.println("PATH: " + fbdo.dataPath());
      Serial.println("TYPE: " + fbdo.dataType());
    }
    else {
      Serial.println("FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }
    count++;
    
    // Write an Float number on the database path test/float
    if (Firebase.RTDB.setFloat(&fbdo, "test/float", 0.01 + random(0,100))){
      Serial.println("PASSED");
      Serial.println("PATH: " + fbdo.dataPath());
      Serial.println("TYPE: " + fbdo.dataType());
    }
    else {
      Serial.println("FAILED");
      Serial.println("REASON: " + fbdo.errorReason());
    }
  }
}
