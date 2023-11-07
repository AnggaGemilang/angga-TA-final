void bh1750() 
{
  float lux = lightMeter.readLightLevel();
  // Serial.print("Light: ");
  // Serial.print(lux);
  // Serial.println(" lux");
  doc["monitoring"]["light_intensity"] = lux;
  Serial.print("Light: ");
  Serial.print(lux);
  Serial.println(" lux");
  
  delay(1000);
  lcd.setCursor(0,0);
  lcd.print("Light  =");
  lcd.setCursor(9,0);
  lcd.print( lux );
  lcd.setCursor(17,0);
  lcd.print("lux");
}
