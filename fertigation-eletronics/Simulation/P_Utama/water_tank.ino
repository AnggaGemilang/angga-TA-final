int waterTank() {
  long duration, waterHeight, volume;
  int distance, percentage;
  
  digitalWrite(WATER_TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(WATER_TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(WATER_TRIG_PIN, LOW);
    
  duration = pulseIn(WATER_ECHO_PIN, HIGH);
    
  distance = duration * SOUND_SPEED/2;
  
  if(distance > TANK_HEIGHT)
    distance = TANK_HEIGHT;
    
  waterHeight = TANK_HEIGHT - distance;
  volume = TANK_SURFACE_AREA * waterHeight;
  percentage = ((double)volume / TANK_MAX_VOLUME) * 100;
    
//  Serial.print("Percentage (%): ");
//  Serial.print(percentage);
//  Serial.println(" %");
//  Serial.print("volume (mL): ");
//  Serial.print(volume);
//  Serial.println(" mL");  

  return percentage;
  
}
