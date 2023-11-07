void phtanah()  
{ 
  bacaSensorPH = analogRead(pinPH); //baca pH
  delay(500);

  nilaiPH = (-0.0139*bacaSensorPH)+7.7851; //rumus pembacaan sensor pH
  doc["monitoring"]["soil_ph"] = nilaiPH;

  lcd.setCursor(0,1);
  lcd.print("Ph Tanah =");
  lcd.setCursor(11,1);
  lcd.print(nilaiPH); 
  delay(5000);
}
