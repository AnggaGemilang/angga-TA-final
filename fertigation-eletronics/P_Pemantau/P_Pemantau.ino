#include <ArduinoJson.h>

#define soilMoisturePin A0
#define waterLevelPin A3

StaticJsonDocument<200> doc;

char output[200];

void soilMoisture();
void waterLevel();

void setup() {
  Serial.begin(9600);
}

void loop() {

  doc["source"] = "P1";
  
  soilMoisture();
  waterLevel();

  serializeJson(doc, output);

}
