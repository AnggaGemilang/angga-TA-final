#include <SPI.h>
#include <LoRa.h>
#define ss 53
#define rst 4
#define dio0 5
#include <SoftwareSerial.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#define ONE_WIRE_BUS 28
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature sensorSuhu(&oneWire);

LiquidCrystal_I2C lcd(0x27,16,2);
int counter = 0;

//Kelembaban
int powerPin = 32;    // untuk pengganti VCC
const int SoilSensor = A0;

//NPK
// SoftwareSerial NPK(3, 1); // RX, TX

//temperature

// Ph tanah
#define pinPH A3  //pin output Sensor PH ditempatkan di D3 //sambungkan kabel hitam (output) ke pin 25
int bacaSensorPH = 0;   //membaca hasil dari sensor pH   
float nilaiPH = 0.0; //nilai pH yang ditampilkan

StaticJsonDocument<200> doc;
char output[200];

void kelembabantanah ();
void Temperature();
void phtanah() ;
void RXTX();

void setup() {
  Serial.begin(9600);
  while (!Serial);
  
  LoRa.setPins(ss, rst, dio0); 
  Serial.println("LoRa Sender");

  if (!LoRa.begin(433E6)) {
    Serial.println("Starting LoRa failed!");
    while (1);
  }

  // NPK.begin(9600);
  
  // Kelembaban tanah
  pinMode(powerPin, OUTPUT);
  // default bernilai LOW
  digitalWrite(powerPin, LOW);
  // mulai komunikasi serial
  
  //ph
  pinMode(pinPH,INPUT);  //inisialisasi pinPH sebagai input

  //lcd
  lcd.init();                      // initialize the lcd 
  // Print a message to the LCD.
  lcd.backlight();


}

void loop() {
  Serial.print("Sending packet: ");
  Serial.println(counter);
  counter++;
  // send packet
  LoRa.beginPacket();
  LoRa.println("Node2");
  LoRa.println(counter);
  doc["source"] = "pendukung";  
  // kelembabantanah();
  Temperature();
  phtanah();
  lcd.clear();
  kelembabantanah();
  lcd.clear();
  //  RXTX();
  serializeJson(doc, output);
  LoRa.endPacket();
  Serial.println("  ");
  delay(5000);


}
