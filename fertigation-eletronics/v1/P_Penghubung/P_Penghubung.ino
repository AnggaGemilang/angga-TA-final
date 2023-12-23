#include <ArduinoJson.h>
#include <painlessMesh.h>
#include <HardwareSerial.h>

#define SOIL_MOISTURE_PIN A0
#define WATER_LEVEL_PIN A3
#define MESH_PREFIX "fertigation-kota203"
#define MESH_PASSWORD "f3rt1g4t10n" 
#define MESH_PORT 5555

Scheduler userScheduler;
painlessMesh  mesh;
HardwareSerial SerialPort(2);

void receivedCallback( uint32_t from, String &msg ) {
  if(msg.length() > 6){
    Serial.printf("Kirim %s\n", msg.c_str());
    SerialPort.print(msg.c_str());
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
  SerialPort.begin(9600, SERIAL_8N1, 16, 17); 
  
  mesh.setDebugMsgTypes( ERROR | STARTUP );
  mesh.init( MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT );
  mesh.onReceive(&receivedCallback);
  mesh.onNewConnection(&newConnectionCallback);
  mesh.onChangedConnections(&changedConnectionCallback);
  mesh.onNodeTimeAdjusted(&nodeTimeAdjustedCallback);
}

void loop() {
  mesh.update();
}
