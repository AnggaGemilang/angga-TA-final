void sensorhujan() 
{
  int rainDigitalVal = analogRead(rainDigital);
  doc["monitoring"]["rainfall"] = rainDigitalVal;
  if (rainDigitalVal <= 2000)
  {
//    LoRa.println("Tidak Hujan");
    keadaan = "Tidak Hujan";
  }
  else if (rainDigitalVal <= 4095)
  {
//    LoRa.println("Hujan");
    keadaan = "Sedang Hujan";
  }
  delay(1000);
  Serial.print ("nilai itensitas hujan =");
  Serial.println(rainDigitalVal);
  lcd.setCursor(0, 1);
  lcd.print("Keadaan=");
  lcd.setCursor(8, 1);
  lcd.print(keadaan);
}
