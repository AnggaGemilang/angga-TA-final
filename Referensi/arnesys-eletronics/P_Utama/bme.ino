void BME () {
//pembacaan data temperature atau suhu 
  doc["monitoring"]["wind_temperature"] = bme.readTemperature();
  lcd.setCursor(0,0);
  lcd.print("Suhu    =");
  lcd.setCursor(10,0);
  lcd.print(bme.readTemperature());
  lcd.setCursor(18,0);
  lcd.print("*C");
//pembacaan data Kelembaban
  doc["monitoring"]["wind_humidity"] = bme.readHumidity();
  lcd.setCursor(0,1);
  lcd.print("kelembaban=");
  lcd.setCursor(12,1);
  lcd.print(bme.readHumidity());
  lcd.setCursor(18,1);
  lcd.print("*%");
//pembacaan data tekanan atmosfer 
  doc["monitoring"]["wind_pressure"] = bme.readPressure() / 100.0F;
  lcd.setCursor(0,2);
  lcd.print("Pressure =");
  lcd.setCursor(11,2);
  lcd.print(bme.readPressure() / 100.0F);
  lcd.setCursor(18,2);
  lcd.print("hPa");
 
//pembacaan data ketinggian berdasarkan permukaan laut
// LoRa.print("Approx. Altitude = ");
// LoRa.print(bme.readAltitude(SEALEVELPRESSURE_HPA));
// LoRa.println(" mdpl");

//pembacaan data temperature atau suhu 
Serial.print("Suhu ="); 
Serial.print(bme.readTemperature());
Serial.println(" *C"); 
 
//pembacaan data Kelembaban
Serial.print("Kelembaban = "); 
Serial.print(bme.readHumidity()); 
Serial.println(" %"); 
 
//pembacaan data tekanan atmosfer 
Serial.print("Pressure = ");
Serial.print(bme.readPressure() / 100.0F);
Serial.println(" hPa");
 
//pembacaan data ketinggian berdasarkan permukaan laut
Serial.print("Approx. Altitude = ");
Serial.print(bme.readAltitude(SEALEVELPRESSURE_HPA));
Serial.println(" mdpl");
}
