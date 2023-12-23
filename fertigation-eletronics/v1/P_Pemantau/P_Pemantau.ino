#include <ArduinoJson.h>
#include <painlessMesh.h>

#define SOIL_MOISTURE_PIN A0
#define WATER_LEVEL_PIN A3
#define MESH_PREFIX "fertigation-kota203"
#define MESH_PASSWORD "f3rt1g4t10n" 
#define MESH_PORT 5555

Scheduler userScheduler;
painlessMesh  mesh;

String temp;

int soilMoisture();
int waterLevel();
void sendMessage();
void sendMessageCallback();

Task taskSendMessage( TASK_SECOND * 5 , TASK_FOREVER, &sendMessage );
Task taskSendMessageCallback( TASK_SECOND * 7 , TASK_FOREVER, &sendMessageCallback );

void sendMessage() {
  DynamicJsonDocument doc(1024);
  doc["source"] = "PP_1";
  doc["moisture"] = soilMoisture();
  doc["water_level"] = waterLevel();

  String msg;
  serializeJson(doc, msg);
  mesh.sendBroadcast( msg );
  
  taskSendMessage.setInterval((TASK_SECOND * 5));
}

void sendMessageCallback() {
  mesh.sendBroadcast( temp );
  taskSendMessageCallback.setInterval((TASK_SECOND * 7));
}

void receivedCallback( uint32_t from, String &msg ) {
  Serial.printf("startHere: Received from %u msg=%s\n", from, msg.c_str());
  temp = msg.c_str();
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

  userScheduler.addTask( taskSendMessage );
  userScheduler.addTask( taskSendMessageCallback );
  taskSendMessage.enable();
  taskSendMessageCallback.enable();
}

void loop() {
  mesh.update();
}
