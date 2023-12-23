int soilMoisture()
{
  // int sensorValue = analogRead(SOIL_MOISTURE_PIN);
  // int convertedMoisture = ( 100 - ( (sensorValue/4095.00) * 100 ) );
  // Serial.print("Moisture = ");
  // Serial.print(convertedMoisture);
  // Serial.println("%");
  return random(1, 5);
  delay(1000);
}
