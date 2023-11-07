// void Sensorwaktu () {
//   DateTime now = rtc.now();
//   // Serial.print(daysOfTheWeek[now.dayOfTheWeek()]);  //hari
//   // Serial.print(", ");
//   // Serial.print(now.day(), DEC);  //tanggal
//   // Serial.print('/');
//   // Serial.print(now.month(), DEC);  //bulan
//   // Serial.print('/');
//   // Serial.print(now.year(), DEC);  //tahun
//   // Serial.print(' ');
//   // Serial.print(now.hour(), DEC);  //jam
//   // Serial.print(':');
//   // Serial.print(now.minute(), DEC);  //tanggal
//   // Serial.print(':');
//   // Serial.print(now.second(), DEC);  //detik
//   // Serial.println();
//   LoRa.print(daysOfTheWeek[now.dayOfTheWeek()]);  //hari
//   LoRa.print(", ");
//   LoRa.print(now.day(), DEC);  //tanggal
//   LoRa.print('/');
//   LoRa.print(now.month(), DEC);  //bulan
//   LoRa.print('/');
//   LoRa.print(now.year(), DEC);  //tahun
//   LoRa.print(' ');
//   LoRa.print(now.hour(), DEC);  //jam
//   LoRa.print(':');
//   LoRa.print(now.minute(), DEC);  //tanggal
//   LoRa.print(':');
//   LoRa.print(now.second(), DEC);  //detik
//   LoRa.printlnF();

//   //Instruksi untuk Menampilkan Data RTC pada LCD
//   /*
//   lcd.setCursor(0,0);
//   lcd.print("Tgl: ");
//   lcd.setCursor(4,0);
//   lcd.print(now.day(), DEC);
//   lcd.setCursor(6,0);
//   lcd.print("/");
//   lcd.setCursor(7,0);
//   lcd.print(now.month(), DEC);
//   lcd.setCursor(9,0);
//   lcd.print("/");
//   lcd.setCursor(10,0);
//   lcd.print(now.year(), DEC);

//   lcd.setCursor(0,1);
//   lcd.print("Jam: ");
//   lcd.setCursor(4,1);
//   lcd.print(now.hour(), DEC);
//   lcd.setCursor(6,1);
//   lcd.print(":");
//   lcd.setCursor(7,1);
//   lcd.print(now.minute(), DEC);
//   lcd.setCursor(9,1);
//   lcd.print(":");
//   lcd.setCursor(10,1);
//   lcd.print(now.second(), DEC);
 
//  */
//   delay(1000);
// }