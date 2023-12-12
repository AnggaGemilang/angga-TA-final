#include <ArduinoJson.h>

#define SOIL_MOISTURE_PIN A0
#define WATER_LEVEL_PIN A3

String temp;

int soilMoisture();
int waterLevel();

void setup() {
  Serial.begin(115200);
}

void loop() {
  DynamicJsonDocument doc(1024);
  doc["source"] = "PP_1";
  doc["moisture"] = soilMoisture();
  doc["water_level"] = waterLevel();

  String msg;
  serializeJson(doc, msg);
  Serial.println(msg);
  delay(5000);
}
