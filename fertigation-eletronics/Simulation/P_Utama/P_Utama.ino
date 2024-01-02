#include "RTClib.h"
#include <LiquidCrystal_I2C.h>
#include <ArduinoJson.h>
#include <WiFi.h>

#define RXp2 16
#define TXp2 17
#define FERTILIZER_TRIG_PIN 5
#define FERTILIZER_ECHO_PIN 18
#define WATER_TRIG_PIN 33
#define WATER_ECHO_PIN 32
#define SOUND_SPEED 0.034
#define LCD_COLUMNS 16
#define LCD_ROWS 2
#define PUMP_RELAY 13
#define VALVE_RELAY_1 14
#define VALVE_RELAY_2 15
#define TANK_SURFACE_AREA 81
#define TANK_HEIGHT 26
#define TANK_MAX_VOLUME 2106

// RTC_DS3231 rtc;
// LiquidCrystal_I2C lcd_i2c(0x27, 16, 2);  

// String timeNow();
// int waterTank();
// int fertilizerTank();

void setup() {
  Serial.begin(115200);

//  lcd_i2c.init();
//  lcd_i2c.backlight();
  
//  pinMode(FERTILIZER_TRIG_PIN, OUTPUT);
//  pinMode(FERTILIZER_ECHO_PIN, INPUT);
  pinMode(VALVE_RELAY_1, OUTPUT);
  pinMode(VALVE_RELAY_2, OUTPUT);
//  pinMode(WATER_TRIG_PIN, OUTPUT);
//  pinMode(WATER_ECHO_PIN, INPUT);

//  lcd_i2c.clear();
//  lcd_i2c.setCursor(3, 0);
//  lcd_i2c.print("Welcome To");
//  lcd_i2c.setCursor(0, 1);
//  lcd_i2c.print("*** KoTA 203 ***");
//
//  if (!rtc.begin()) {
//    Serial.println("Couldn't find RTC");
//    while(1);
//  }
//
//  rtc.adjust(DateTime(__DATE__, __TIME__));
}

void loop() {
  digitalWrite(VALVE_RELAY_2, HIGH);
  digitalWrite(VALVE_RELAY_1, HIGH);

  delay(20000);
//
//  digitalWrite(VALVE_RELAY_1, LOW);
//  digitalWrite(VALVE_RELAY_2, HIGH);
//
//  delay(5000);
//  digitalWrite(VALVE_RELAY_2, LOW);
  
  //  lcd_i2c.clear();
  //  lcd_i2c.setCursor(0, 0);
  //  lcd_i2c.print("Moisture(1): 20%");
  //  lcd_i2c.setCursor(0, 1);
  //  lcd_i2c.print("Water(2): 20 cm");

  //  digitalWrite(PUMP_RELAY, LOW);

  //  lcd_i2c.clear();
  //  lcd_i2c.setCursor(0, 0);
  //  lcd_i2c.print("Moisture(2): 20%");
  //  lcd_i2c.setCursor(0, 1);
  //  lcd_i2c.print("Water(2): 20 cm");

  //  lcd_i2c.clear();
  //  lcd_i2c.setCursor(0, 0);
  //  lcd_i2c.print("Water: 20");
  //  lcd_i2c.setCursor(0, 1);
  //  lcd_i2c.print("Fertilizer: 20%");
  
//  Serial.print("Waktu: ");
//  Serial.println(timeNow());
//  Serial.print("Fertilizer Tank: ");
//  Serial.println(fertilizerTank());
//  Serial.print("Water Tank: ");
//  Serial.println(waterTank());    
//  Serial.println("============");
//  delay(2000);
}
