void kelembabantanah ()
{
 // Serial.print("Persentase Kelembaban Tanah = ");
 digitalWrite(powerPin, HIGH);
 delay(2000);
 float kelembabanTanah;

 int hasilPembacaan = analogRead(SoilSensor);
 kelembabanTanah = (100 - ((hasilPembacaan/1023.00)*100));
 doc["monitoring"]["soil_humidity"] = kelembabanTanah;

 lcd.setCursor(0,0);
 lcd.print("K Tanah=");
 lcd.setCursor(10,0);
 lcd.print(kelembabanTanah);
 lcd.setCursor(15,0);
 lcd.print("%");

 
 delay (1000);
 digitalWrite(powerPin, LOW);
 delay (1000);


}
