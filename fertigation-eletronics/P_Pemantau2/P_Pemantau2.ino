#include <ArduinoJson.h>
#include <painlessMesh.h>

#define SOIL_MOISTURE_PIN A0
#define WATER_LEVEL_PIN A3
#define MESH_PREFIX "fertigation-kota203"
#define MESH_PASSWORD "f3rt1g4t10n" 
#define MESH_PORT 5555

char output[200];

StaticJsonDocument<200> doc;
Scheduler userScheduler;
painlessMesh  mesh;

void soilMoisture();
void waterLevel();
void sendMessage();

Task taskSendMessage( TASK_SECOND * 1 , TASK_FOREVER, &sendMessage );

void sendMessage() {
  String msg = "Hi from node2";
  msg += mesh.getNodeId();
  mesh.sendBroadcast( msg );
  taskSendMessage.setInterval( random( TASK_SECOND * 1, TASK_SECOND * 5 ));
}

void receivedCallback( uint32_t from, String &msg ) {
  Serial.printf("startHere: Received from %u msg=%s\n", from, msg.c_str());
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
  Serial.begin(9600);
  mesh.setDebugMsgTypes( ERROR | STARTUP );

  mesh.init( MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT );
  mesh.onReceive(&receivedCallback);
  mesh.onNewConnection(&newConnectionCallback);
  mesh.onChangedConnections(&changedConnectionCallback);
  mesh.onNodeTimeAdjusted(&nodeTimeAdjustedCallback);

  userScheduler.addTask( taskSendMessage );
  taskSendMessage.enable();
}

void loop() {

  mesh.update();

  doc["source"] = "P1";
  
  // soilMoisture();
  // waterLevel();

  // serializeJson(doc, output);

}
