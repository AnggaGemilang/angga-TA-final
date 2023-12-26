int fertilizerTank() {
  long duration, volume;
  int distance, percentage, waterHeight;
    
  digitalWrite(FERTILIZER_TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(FERTILIZER_TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(FERTILIZER_TRIG_PIN, LOW);
    
  duration = pulseIn(FERTILIZER_ECHO_PIN, HIGH);
    
  distance = duration * SOUND_SPEED/2;
  
  if(distance > TANK_HEIGHT)
    distance = TANK_HEIGHT;
    
  waterHeight = TANK_HEIGHT - distance; 
  volume = TANK_SURFACE_AREA * waterHeight; 
  percentage = ((double)volume / TANK_MAX_VOLUME) * 100;
  return percentage;
}
