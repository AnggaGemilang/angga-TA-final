void setup() {
  Serial.begin(115200);
}

void loop() {
   Serial.printf("Moisture = %d\n", random(4000, 4005));
   delay(1000);
   Serial.printf("Moisture = %d\n", random(4000, 4005));
   delay(1000);
   Serial.printf("Moisture = %d\n", random(4000, 4005));
   delay(1000);
//   int moisture1 = random(4000, 4005);
//   Serial.printf("Moisture = %d\n", random(4000, 4005));
//   Serial.printf("Converted Moisture = %d%\n", (int)100 - ( (moisture1/4095.00) * 100 ) );
//   delay(1000);
//   int moisture2 = random(4000, 4005);
//   Serial.printf("Moisture = %d\n", random(4000, 4005));
//   Serial.printf("Converted Moisture = %d%\n", (int)100 - ( (moisture2/4095.00) * 100 ) );
//   delay(1000);
//   int moisture3 = random(4000, 4005);
//   Serial.printf("Moisture = %d\n", random(4000, 4005));
//   Serial.printf("Converted Moisture = %d%\n", (int)100 - ( (moisture3/4095.00) * 100 ) );   
//   delay(1000);
   Serial.printf("Water Level = %d\n", random(800, 905));
   delay(1000);
   Serial.printf("Water Level = %d\n", random(800, 905));
   delay(1000);
   Serial.printf("Water Level = %d\n", random(800, 905));
}
