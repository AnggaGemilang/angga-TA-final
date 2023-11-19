void waterLevel() {
  int sensorValue = analogRead(WATER_LEVEL_PIN);
  doc["water_level"] = sensorValue;
  Serial.print("Water Level = ");
  Serial.println(sensorValue);
  delay(1000);
}
