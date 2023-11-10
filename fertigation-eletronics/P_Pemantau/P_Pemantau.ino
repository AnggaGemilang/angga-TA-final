#include <ArduinoJson.h>

#define SOIL_MOISTURE_PIN A0
#define WATER_LEVEL_PIN A3

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
