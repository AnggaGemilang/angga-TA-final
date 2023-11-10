#define FERTILIZER_TRIG_PIN 5
#define FERTILIZER_ECHO_PIN 18
#define WATER_TRIG_PIN 33
#define WATER_ECHO_PIN 32
#define SOUND_SPEED 0.034
#define TANK_SURFACE_AREA 81
#define TANK_HEIGHT 26
#define TANK_MAX_VOLUME 2106

#include "RTClib.h"
#include "string.h"

RTC_DS3231 rtc;

void waterTank();
void fertilizerTank();
String timeNow();

void setup() {
  Serial.begin(9600);
  pinMode(FERTILIZER_TRIG_PIN, OUTPUT);
  pinMode(FERTILIZER_ECHO_PIN, INPUT);
  pinMode(WATER_TRIG_PIN, OUTPUT);
  pinMode(WATER_ECHO_PIN, INPUT);

  if (!rtc.begin()) {
    Serial.println("Couldn't find RTC");
    while(1);
  }

  rtc.adjust(DateTime(__DATE__, __TIME__));
}

void loop() {
  // fertilizerTank();
  // waterTank();

  DateTime now = rtc.now();

  Serial.print("Waktu: ");
  Serial.println(timeNow());

  delay(1000);
}
