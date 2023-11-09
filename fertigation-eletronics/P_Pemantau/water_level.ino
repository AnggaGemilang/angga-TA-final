void waterLevel() {
  int sensorValue = analogRead(waterLevelPin);
  doc["water_level"] = sensorValue;
  Serial.print("Water Level = ");
  Serial.println(sensorValue);
  delay(1000);
}
