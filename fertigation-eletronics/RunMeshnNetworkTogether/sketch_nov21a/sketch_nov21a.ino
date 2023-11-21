#include <ArduinoJson.h>
#include <FirebaseESP32.h>
#include <painlessMesh.h>
#include <WiFi.h>

#define WIFI_SSID "SPEEDY"
#define WIFI_PASSWORD "suherman"
#define HARDWARE_CODE "13kjh123kj1h3j12h21312kjhasdasd"
#define OWNER_CODE "hpoQA4Xv0hTpmsB3lgOXyrRF7S12"
#define FIREBASE_HOST "https://fertigation-system-389e8-default-rtdb.firebaseio.com/"
#define FIREBASE_AUTH "AIzaSyBZvwV5-74YkBUlphAYpuyFsHIQVyfRHW4"
#define MESH_PREFIX "fertigation-kota203"
#define MESH_PASSWORD "f3rt1g4t10n" 
#define MESH_PORT 5555

FirebaseData fbdo;
Scheduler userScheduler;
painlessMesh  mesh;

String monitorDeviceData;

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
  mesh.setDebugMsgTypes( ERROR | STARTUP );
  mesh.init( MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT );
  mesh.onReceive(&receivedCallback);
  mesh.onNewConnection(&newConnectionCallback);
  mesh.onChangedConnections(&changedConnectionCallback);
  mesh.onNodeTimeAdjusted(&nodeTimeAdjustedCallback);
  mesh.stationManual(WIFI_SSID, WIFI_PASSWORD);
  mesh.setRoot(true);
  mesh.setContainsRoot(true);
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Serial.print("Firebase Connected");
}


void loop() {
  mesh.update();
  if (Firebase.setInt(fbdo,"/monitor/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/13kjh123kj1h3j12h21312kjhasdasd/pemantau_1/moisture", 1)) {
    Serial.printf("Upload to pemantau 1\n");
  } else {
    Serial.println(fbdo.errorReason());
  }
  delay(2000);
}
