void soilMoisture()
{
  int sensorValue = analogRead(SOIL_MOISTURE_PIN);
  int convertedMoisture = ( 100 - ( (sensorValue/4095.00) * 100 ) );
  doc["soil_moisture"] = convertedMoisture;
  Serial.print("Moisture = ");
  Serial.print(convertedMoisture);
  Serial.println("%");
  delay(1000);
}
