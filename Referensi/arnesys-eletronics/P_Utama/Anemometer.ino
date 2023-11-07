
void anemometer()
{
  float knot;
  //Measure RPM
  if ((millis() - timeold) >= timemeasure * 1000)
  {
    countThing++;
    detachInterrupt(digitalPinToInterrupt(GPIO_pulse)); // Disable interrupt when calculating
    rps = float(rpmcount) / float(timemeasure);         // rotations per second
    rpm = 60 * rps;                                     // rotations per minute
    omega = 2 * PI * rps;                               // rad/s
    velocity_ms = omega * radius * calibration_value;   // m/s
    velocity_kmh = velocity_ms * 3.6;                   // km/h
    knot = 1.8 * velocity_kmh;
    doc["monitoring"]["wind_speed"] = knot;
    // Serial.print("rps=");
    // Serial.print(rps);
    // Serial.print("   rpm=");
    // Serial.print(rpm);
    // Serial.print("   velocity_ms=");
    // Serial.print(velocity_ms);
    // Serial.print("   velocity_kmh=");
    // Serial.print(velocity_kmh);
    // Serial.println("   ");
    if (countThing == 1) // Send data per 25 seconds
    {
      Serial.println("Send data to server");
      countThing = 0;
    }
    timeold = millis();
    rpmcount = 0;
    attachInterrupt(digitalPinToInterrupt(GPIO_pulse), rpm_anemometer, RISING); // enable interrupt
  }
  lcd.setCursor(0,2);
  lcd.print("Kec Angin=");
  lcd.setCursor(11,2);
  lcd.print(knot);
  lcd.setCursor(18,2);
  lcd.print("knot");
} // end of loop

void rpm_anemometer()
{
  if (long(micros() - last_micros) >= 5000)
  { // time to debounce measures
    rpmcount++;
    last_micros = micros();
  }
}
