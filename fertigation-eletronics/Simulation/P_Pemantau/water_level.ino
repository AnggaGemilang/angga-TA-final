int waterLevel() {
  int sensorValue = analogRead(WATER_LEVEL_PIN);
  Serial.print("Water Level = ");
  Serial.println(sensorValue);
  return sensorValue;
}
