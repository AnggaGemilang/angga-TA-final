#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <WiFi.h>
#include <HTTPClient.h>
#include <Fuzzy.h>
#include <SPI.h>
#include <LoRa.h>

#define MSG_BUFFER_SIZE (50)
#define MQTT_SERVER "test.mosquitto.org"
#define MQTT_PORT 1883
#define WIFI_SSID "Galaxy M33 5G"
#define WIFI_PASSWORD "anggaganteng"
#define ss 5
#define rst 14
#define dio0 16

//#define WIFI_SSID "Student_Polban"
//#define WIFI_PASSWORD "20polban21"
//#define WIFI_SSID "SPEEDY"
//#define WIFI_PASSWORD "suherman"
//#define WIFI_SSID "kostankuning@wifi.id"
//#define WIFI_PASSWORD "kostankuning14"

StaticJsonDocument<70> in;
HTTPClient httpMainDevice, httpSupportDevice;
WiFiClient espClient;
PubSubClient client(espClient);

Fuzzy *fuzzy = new Fuzzy();

char FIELD_CODE[50] = "cd3eb1ad-f3ee-4fa4-bf19-0e4f7da89746";
char SERVER1[58] = "https://arnesys.agrapana.tech/api/monitoring-main-devices";
char SERVER2[61] = "https://arnesys.agrapana.tech/api/monitoring-support-devices";
char topic[100] = "";
long lastMsg = 0;
long lastMsg2 = 0;

// FuzzySet Input Suhu
FuzzySet *suhuDingin = new FuzzySet(0, 13.5, 13.5, 27);
FuzzySet *suhuNormal = new FuzzySet(25, 28, 28, 31);
FuzzySet *suhuPanas = new FuzzySet(29, 34.5, 34.5, 40);
  
// FuzzySet Input Kecepatan Udara
FuzzySet *kecepatanPelan = new FuzzySet(0, 2, 2, 4);
FuzzySet *kecepatanSedang = new FuzzySet(2, 5, 5, 8);
FuzzySet *kecepatanKencang = new FuzzySet(6, 10.5, 10.5, 15);
  
// FuzzySet Input Kelembaban
FuzzySet *kelembabanKering = new FuzzySet(50, 70, 75, 80);
FuzzySet *kelembabanSedang = new FuzzySet(75, 82.5, 82.5, 90);
FuzzySet *kelembabanBasah = new FuzzySet(85, 90, 95, 100);
  
// FuzzySet Output Prediksi Cuaca
FuzzySet *cerahBerawan = new FuzzySet(0, 2.5, 2.5, 5);
FuzzySet *hujanRingan = new FuzzySet(2.5, 11.25, 11.25, 20);
FuzzySet *hujanSedang = new FuzzySet(15, 32.5, 32.5, 50);
FuzzySet *hujanLebat = new FuzzySet(45, 72.5, 72.5, 100);  

char jenis[50], output[200], output2[150];
unsigned int windTemperature, windHumidity, windPressure, windSpeed, lightIntensity, rainfall;
unsigned int soilTemperature, soilHumidity, soilPh, soilNitrogen, soilPhosphor, soilKalium;
char weatherForecast[50], pestsPrediction[100];
String LoRaData;

double u0[] = {0.40427292089515005, -0.6348080638911874, -0.5826808357272043};
double u1[] = {-0.48512750507418756, 0.761769676669425, 0.6992170028726451};

double p0[] = {3.5232428618986744, 1.8252391089351945, 60.277945706722875};
double p1[] = {0.7003134205554864, 2.0890725392537743, 0.7789572738382433};

double c = 6.194008413911195;

void setup() 
{
  Serial.begin(9600);
  while (!Serial);
  setup_lora();
  setup_wifi();
  inisialisasiFuzzyInputOutput();
  inisialisasiFuzzyRule();
  client.setServer(MQTT_SERVER, MQTT_PORT);
}

void loop() 
{
  if (!client.connected()) 
  {
    reconnect();
  }
  client.loop();

  int packetSize = LoRa.parsePacket();
  if (packetSize) 
  {
    Serial.print("Received packet ");

    while (LoRa.available()) 
    {
      LoRaData = LoRa.readString();
      rcvCommand();
    }

    Serial.println();
  }  
}

void rcvCommand()
{
    Serial.println(LoRaData);
    deserializeJson(in, LoRaData);
//    command_rcvd_id = in["id"];
//    command_rcvd_temp = in["suhu"];
//    command_rcvd_hum = in["hum"];

  if (jenis == "faster") 
  {
    Serial.println("");

    windTemperature = random(25, 28);
    windHumidity = random(40, 43);
    windPressure = random(13, 15);
    windSpeed = random(6, 9);
    rainfall = random(0, 100);
    lightIntensity = random(2800, 3200);

//    Kirim data perangkat utama

    StaticJsonDocument<96> doc;

    doc["monitoring"]["wind_temperature"] = windTemperature;
    doc["monitoring"]["wind_humidity"] = windHumidity;
    doc["monitoring"]["wind_pressure"] = windPressure;
    doc["monitoring"]["wind_speed"] = windSpeed;
    doc["monitoring"]["rainfall"] = rainfall;
    doc["monitoring"]["light_intensity"] = lightIntensity;
    serializeJson(doc, output); 

    strcpy(topic, "arnesys/");
    strcat(topic, FIELD_CODE);
    strcat(topic, "/utama");
    Serial.print("Topic: ");
    Serial.println(topic);
    
    client.publish(topic, output);
    Serial.print("Publish message: ");
    Serial.println(output);

//    Kirim data perangkat pendukung

    Serial.println("");

    StaticJsonDocument<200> doc2;

    doc2["monitoring"]["soil_temperature"] = soilTemperature;
    doc2["monitoring"]["soil_humidity"] = soilHumidity;
    doc2["monitoring"]["soil_ph"] = soilPh;
    doc2["monitoring"]["soil_nitrogen"] = soilNitrogen;
    doc2["monitoring"]["soil_phosphor"] = soilPhosphor;
    doc2["monitoring"]["soil_kalium"] = soilKalium;
    serializeJson(doc2, output); 

    strcpy(topic, "arnesys/");
    strcat(topic, FIELD_CODE);
    strcat(topic, "/pendukung/1");
    Serial.print("Topic: ");
    Serial.println(topic);

    client.publish(topic, output);
    Serial.print("Publish message: ");
    Serial.println(output);

    Serial.println("");
    Serial.println("=====================================");    

  } else if (jenis == "slower") 
  {
    Serial.println("");

    soilTemperature = random(25, 28);
    soilHumidity = random(40, 43);
    soilPh = random(5, 7);
    soilNitrogen = random(8, 9);
    soilPhosphor = random(2, 3);
    soilKalium = random(5, 6);

//    Fuzzy Logic

    fuzzy->setInput(1, windTemperature);
    fuzzy->setInput(2, windSpeed);
    fuzzy->setInput(3, windHumidity);
    fuzzy->fuzzify();
    float output = fuzzy->defuzzify(1);

    if(output <= 5)
    {
      strcpy(weatherForecast,"Cerah-Berawan");
    } else if(output > 5 && output <= 20)
    {
      strcpy(weatherForecast,"Hujan Ringan");
    } else if(output > 20 && output <= 50)
    {
      strcpy(weatherForecast,"Hujan Sedang");
    } else
    {
      strcpy(weatherForecast,"Hujan Lebat");
    } 

//    Naive Bayes 

    float data[3]= {soilTemperature, soilHumidity, rainfall};

    double term1 = 0.0;
    double term2 = 0.0;

    for(int i=0; i<3; i++)
    {
        term1 += p0[i] * (data[i] - u0[i]) * (data[i] - u0[i]);
        term2 += p1[i] * (data[i] - u1[i]) * (data[i] - u1[i]);
    }

    double temp = term1 - term2;

    if(temp >= c)
    {
      strcpy(pestsPrediction,"ulat_daun=tinggi,ulat_krop=tinggi,busuk_hitam=tinggi"); 
    }
    else
    {
      strcpy(pestsPrediction,"ulat_daun=rendah,ulat_krop=rendah,busuk_hitam=rendah");
    }

//    Kirim data AI

    StaticJsonDocument<200> doc3;

    doc3["ai_processing"]["weather_forecast"] = weatherForecast;
    doc3["ai_processing"]["pests_prediction"] = pestsPrediction;
    serializeJson(doc3, output2);

    strcpy(topic, "arnesys/");
    strcat(topic, FIELD_CODE);
    strcat(topic, "/utama/ai");
    Serial.print("Topic: ");
    Serial.println(topic);
    
    client.publish(topic, output2);
    Serial.print("Publish message: ");
    Serial.println(output2);

    Serial.println("");
    Serial.println("=====================================");

//    Kirim data perangkat utama

    httpMainDevice.begin(SERVER1);

    httpMainDevice.addHeader("Content-Type", "application/x-www-form-urlencoded");
    String httpRequestData = "&wind_temperature=" + String(windTemperature) + "&wind_humidity=" + String(windHumidity) + "&wind_pressure=" + String(windPressure) + "&wind_speed=" + String(windSpeed) + "&rainfall=" + String(rainfall) + "&light_intensity=" + String(lightIntensity) + "&field_id=" + String(FIELD_CODE);
    int httpResponseCode = httpMainDevice.POST(httpRequestData);
           
    Serial.print("HTTP Response code is: ");
    Serial.println(httpResponseCode);
    httpMainDevice.end();

//    Kirim data perangkat pendukung

    Serial.println("");

    httpSupportDevice.begin(SERVER2);

    httpSupportDevice.addHeader("Content-Type", "application/x-www-form-urlencoded");
    String httpRequestData2 = "&number_of=1&soil_temperature=" + String(soilTemperature) + "&soil_humidity=" + String(soilHumidity) + "&soil_ph=" + String(soilPh) + "&soil_nitrogen=" + String(soilNitrogen) + "&soil_phosphor=" + String(soilPhosphor) + "&soil_kalium=" + String(soilKalium) + "&field_id=" + String(FIELD_CODE);
    int httpResponseCode2 = httpSupportDevice.POST(httpRequestData2);
    
    Serial.print("HTTP Response code is: ");
    Serial.println(httpResponseCode2);
    httpSupportDevice.end();

    Serial.println("");
    Serial.println("=====================================");
  } 
}

void setup_lora() 
{
  LoRa.setPins(ss, rst, dio0);
  if (!LoRa.begin(433E6)) {
    Serial.println("Starting LoRa failed!");
    while (1);
  }
  Serial.println("LoRa Initializing OK!");
}

void setup_wifi() 
{
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID);
  Serial.println();

  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  randomSeed(micros());

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}

void inisialisasiFuzzyInputOutput()
{
  // FuzzyInput suhu
  FuzzyInput *temperature = new FuzzyInput(1);
  temperature->addFuzzySet(suhuDingin);
  temperature->addFuzzySet(suhuNormal);
  temperature->addFuzzySet(suhuPanas);
  fuzzy->addFuzzyInput(temperature);
    
  // FuzzyInput kecepatan angin
  FuzzyInput *windSpeed = new FuzzyInput(2);
  windSpeed->addFuzzySet(kecepatanPelan);
  windSpeed->addFuzzySet(kecepatanSedang);
  windSpeed->addFuzzySet(kecepatanKencang);
  fuzzy->addFuzzyInput(windSpeed);
    
  // FuzzyInput kelembaban
  FuzzyInput *humidity = new FuzzyInput(3);
  humidity->addFuzzySet(kelembabanKering);
  humidity->addFuzzySet(kelembabanSedang);
  humidity->addFuzzySet(kelembabanBasah);
  fuzzy->addFuzzyInput(humidity);

  // ===================================================

  // FuzzyOutput kondisi cuaca
  FuzzyOutput *prediksiCuaca = new FuzzyOutput(1);
  prediksiCuaca->addFuzzySet(cerahBerawan);
  prediksiCuaca->addFuzzySet(hujanRingan);
  prediksiCuaca->addFuzzySet(hujanSedang);
  prediksiCuaca->addFuzzySet(hujanLebat);
  fuzzy->addFuzzyOutput(prediksiCuaca);
}

void inisialisasiFuzzyRule()
{
  FuzzyRuleAntecedent *dingin_pelan = new FuzzyRuleAntecedent();
  dingin_pelan->joinWithAND(suhuDingin, kecepatanPelan);

  FuzzyRuleAntecedent *dingin_sedang = new FuzzyRuleAntecedent();
  dingin_sedang->joinWithAND(suhuDingin, kecepatanSedang);

  FuzzyRuleAntecedent *dingin_kencang = new FuzzyRuleAntecedent();
  dingin_kencang->joinWithAND(suhuDingin, kecepatanKencang);

  FuzzyRuleAntecedent *normal_pelan = new FuzzyRuleAntecedent();
  normal_pelan->joinWithAND(suhuNormal, kecepatanPelan);

  FuzzyRuleAntecedent *normal_sedang = new FuzzyRuleAntecedent();
  normal_sedang->joinWithAND(suhuNormal, kecepatanSedang);

  FuzzyRuleAntecedent *normal_kencang = new FuzzyRuleAntecedent();
  normal_kencang->joinWithAND(suhuNormal, kecepatanKencang);

  FuzzyRuleAntecedent *panas_pelan = new FuzzyRuleAntecedent();
  panas_pelan->joinWithAND(suhuPanas, kecepatanPelan);

  FuzzyRuleAntecedent *panas_sedang = new FuzzyRuleAntecedent();
  panas_sedang->joinWithAND(suhuPanas, kecepatanSedang);

  FuzzyRuleAntecedent *panas_kencang = new FuzzyRuleAntecedent();
  panas_kencang->joinWithAND(suhuPanas, kecepatanKencang);

  FuzzyRuleAntecedent *basah = new FuzzyRuleAntecedent();
  basah->joinSingle(kelembabanBasah);

  FuzzyRuleAntecedent *sedang = new FuzzyRuleAntecedent();
  sedang->joinSingle(kelembabanSedang);

  FuzzyRuleAntecedent *kering = new FuzzyRuleAntecedent();
  kering->joinSingle(kelembabanKering);

  FuzzyRuleConsequent *hujan_ringan = new FuzzyRuleConsequent();
  hujan_ringan->addOutput(hujanRingan);

  FuzzyRuleConsequent *hujan_sedang = new FuzzyRuleConsequent();
  hujan_sedang->addOutput(hujanSedang);

  FuzzyRuleConsequent *hujan_lebat = new FuzzyRuleConsequent();
  hujan_lebat->addOutput(hujanLebat);

  FuzzyRuleConsequent *cerah_berawan = new FuzzyRuleConsequent();
  cerah_berawan->addOutput(cerahBerawan);
  
  // Rule 1

  FuzzyRuleAntecedent *dingin_pelan_basah_1 = new FuzzyRuleAntecedent();
  dingin_pelan_basah_1->joinWithAND(dingin_pelan, basah);  

  FuzzyRule *fuzzyRule01 = new FuzzyRule(1, dingin_pelan_basah_1, hujan_ringan);
  fuzzy->addFuzzyRule(fuzzyRule01);

  // Rule 2
 
  FuzzyRuleAntecedent *dingin_pelan_sedang_2 = new FuzzyRuleAntecedent();
  dingin_pelan_sedang_2->joinWithAND(dingin_pelan, sedang);  

  FuzzyRule *fuzzyRule02 = new FuzzyRule(2, dingin_pelan_sedang_2, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule02);

  // Rule 3
 
  FuzzyRuleAntecedent *dingin_pelan_kering_3 = new FuzzyRuleAntecedent();
  dingin_pelan_kering_3->joinWithAND(dingin_pelan, kering);  

  FuzzyRule *fuzzyRule03 = new FuzzyRule(3, dingin_pelan_kering_3, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule03);

  // Rule 4
  
  FuzzyRuleAntecedent *dingin_sedang_basah_4 = new FuzzyRuleAntecedent();
  dingin_sedang_basah_4->joinWithAND(dingin_sedang, basah);  

  FuzzyRule *fuzzyRule04 = new FuzzyRule(4, dingin_sedang_basah_4, hujan_sedang);
  fuzzy->addFuzzyRule(fuzzyRule04);

  // Rule 5
  
  FuzzyRuleAntecedent *dingin_sedang_sedang_5 = new FuzzyRuleAntecedent();
  dingin_sedang_sedang_5->joinWithAND(dingin_sedang, sedang);  

  FuzzyRule *fuzzyRule05 = new FuzzyRule(5, dingin_sedang_sedang_5, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule05);

  // Rule 6

  FuzzyRuleAntecedent *dingin_sedang_kering_6 = new FuzzyRuleAntecedent();
  dingin_sedang_kering_6->joinWithAND(dingin_sedang, kering);  
  
  FuzzyRule *fuzzyRule06 = new FuzzyRule(6, dingin_sedang_kering_6, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule06);

  // Rule 7
  
  FuzzyRuleAntecedent *dingin_kencang_basah_7 = new FuzzyRuleAntecedent();
  dingin_kencang_basah_7->joinWithAND(dingin_kencang, basah);
  
  FuzzyRule *fuzzyRule07 = new FuzzyRule(7, dingin_kencang_basah_7, hujan_lebat);
  fuzzy->addFuzzyRule(fuzzyRule07);

  // Rule 8
  
  FuzzyRuleAntecedent *dingin_kencang_sedang_8 = new FuzzyRuleAntecedent();
  dingin_kencang_sedang_8->joinWithAND(dingin_kencang, sedang);
  
  FuzzyRule *fuzzyRule08 = new FuzzyRule(8, dingin_kencang_sedang_8, hujan_lebat);
  fuzzy->addFuzzyRule(fuzzyRule08);

  // Rule 9
  
  FuzzyRuleAntecedent *dingin_kencang_kering_9 = new FuzzyRuleAntecedent();
  dingin_kencang_kering_9->joinWithAND(dingin_kencang, kering);
  
  FuzzyRule *fuzzyRule09 = new FuzzyRule(9, dingin_kencang_kering_9, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule09);

  // Rule 10
  
  FuzzyRuleAntecedent *normal_pelan_basah_10 = new FuzzyRuleAntecedent();
  normal_pelan_basah_10->joinWithAND(normal_pelan, basah);
  
  FuzzyRule *fuzzyRule10 = new FuzzyRule(10, normal_pelan_basah_10, hujan_ringan);
  fuzzy->addFuzzyRule(fuzzyRule10);

  // Rule 11
  
  FuzzyRuleAntecedent *normal_pelan_sedang_11 = new FuzzyRuleAntecedent();
  normal_pelan_sedang_11->joinWithAND(normal_pelan, sedang);

  FuzzyRule *fuzzyRule11 = new FuzzyRule(11, normal_pelan_sedang_11, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule11);

  // Rule 12
  
  FuzzyRuleAntecedent *normal_pelan_kering_12 = new FuzzyRuleAntecedent();
  normal_pelan_kering_12->joinWithAND(normal_pelan, kering);
  
  FuzzyRule *fuzzyRule12 = new FuzzyRule(12, normal_pelan_kering_12, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule12);

  // Rule 13
  
  FuzzyRuleAntecedent *normal_sedang_basah_13 = new FuzzyRuleAntecedent();
  normal_sedang_basah_13->joinWithAND(normal_sedang, basah);
  
  FuzzyRule *fuzzyRule13 = new FuzzyRule(13, normal_sedang_basah_13, hujan_sedang);
  fuzzy->addFuzzyRule(fuzzyRule13);

  // Rule 14
  
  FuzzyRuleAntecedent *normal_sedang_sedang_14 = new FuzzyRuleAntecedent();
  normal_sedang_sedang_14->joinWithAND(normal_sedang, sedang);
  
  FuzzyRule *fuzzyRule14 = new FuzzyRule(14, normal_sedang_sedang_14, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule14);

  // Rule 15
  
  FuzzyRuleAntecedent *normal_sedang_kering_15 = new FuzzyRuleAntecedent();
  normal_sedang_kering_15->joinWithAND(normal_sedang, kering);
  
  FuzzyRule *fuzzyRule15 = new FuzzyRule(15, normal_sedang_kering_15, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule15);

  // Rule 16
  
  FuzzyRuleAntecedent *normal_kencang_basah_16 = new FuzzyRuleAntecedent();
  normal_kencang_basah_16->joinWithAND(normal_kencang, basah);
  
  FuzzyRule *fuzzyRule16 = new FuzzyRule(16, normal_kencang_basah_16, hujan_lebat);
  fuzzy->addFuzzyRule(fuzzyRule16);

  // Rule 17
  
  FuzzyRuleAntecedent *normal_kencang_sedang_17 = new FuzzyRuleAntecedent();
  normal_kencang_sedang_17->joinWithAND(normal_kencang, sedang);

  FuzzyRule *fuzzyRule17 = new FuzzyRule(17, normal_kencang_sedang_17, hujan_sedang);
  fuzzy->addFuzzyRule(fuzzyRule17);

  // Rule 18
  
  FuzzyRuleAntecedent *normal_kencang_kering_18 = new FuzzyRuleAntecedent();
  normal_kencang_kering_18->joinWithAND(normal_kencang, kering);
 
  FuzzyRule *fuzzyRule18 = new FuzzyRule(18, normal_kencang_kering_18, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule18);

  // Rule 19
  
  FuzzyRuleAntecedent *panas_pelan_basah_19 = new FuzzyRuleAntecedent();
  panas_pelan_basah_19->joinWithAND(panas_pelan, basah);
  
  FuzzyRule *fuzzyRule19 = new FuzzyRule(19, panas_pelan_basah_19, hujan_sedang);
  fuzzy->addFuzzyRule(fuzzyRule19);

  // Rule 20
  
  FuzzyRuleAntecedent *panas_pelan_sedang_20 = new FuzzyRuleAntecedent();
  panas_pelan_sedang_20->joinWithAND(panas_pelan, sedang);
  
  FuzzyRule *fuzzyRule20 = new FuzzyRule(20, panas_pelan_sedang_20, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule20);

  // Rule 21
  
  FuzzyRuleAntecedent *panas_pelan_kering_21 = new FuzzyRuleAntecedent();
  panas_pelan_kering_21->joinWithAND(panas_pelan, kering);
  
  FuzzyRule *fuzzyRule21 = new FuzzyRule(21, panas_pelan_kering_21, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule21);

  // Rule 22
  
  FuzzyRuleAntecedent *panas_sedang_basah_22 = new FuzzyRuleAntecedent();
  panas_sedang_basah_22->joinWithAND(panas_sedang, basah);
  
  FuzzyRule *fuzzyRule22 = new FuzzyRule(22, panas_sedang_basah_22, hujan_ringan);
  fuzzy->addFuzzyRule(fuzzyRule22);

  // Rule 23
    
  FuzzyRuleAntecedent *panas_sedang_sedang_23 = new FuzzyRuleAntecedent();
  panas_sedang_sedang_23->joinWithAND(panas_sedang, sedang);
  
  FuzzyRule *fuzzyRule23 = new FuzzyRule(23, panas_sedang_sedang_23, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule23);

  // Rule 24
  
  FuzzyRuleAntecedent *panas_sedang_kering_24 = new FuzzyRuleAntecedent();
  panas_sedang_kering_24->joinWithAND(panas_sedang, kering);
  
  FuzzyRule *fuzzyRule24 = new FuzzyRule(24, panas_sedang_kering_24, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule24);

  // Rule 25
  
  FuzzyRuleAntecedent *panas_kencang_basah_25 = new FuzzyRuleAntecedent();
  panas_kencang_basah_25->joinWithAND(panas_kencang, basah);
  
  FuzzyRule *fuzzyRule25 = new FuzzyRule(25, panas_kencang_basah_25, hujan_lebat);
  fuzzy->addFuzzyRule(fuzzyRule25);

  // Rule 26
  
  FuzzyRuleAntecedent *panas_kencang_sedang_26 = new FuzzyRuleAntecedent();
  panas_kencang_sedang_26->joinWithAND(panas_kencang, sedang);
  
  FuzzyRule *fuzzyRule26 = new FuzzyRule(26, panas_kencang_sedang_26, hujan_ringan);
  fuzzy->addFuzzyRule(fuzzyRule26);

  // Rule 27
  
  FuzzyRuleAntecedent *panas_kencang_kering_27 = new FuzzyRuleAntecedent();
  panas_kencang_kering_27->joinWithAND(panas_kencang, kering);
  
  FuzzyRule *fuzzyRule27 = new FuzzyRule(27, panas_kencang_kering_27, cerah_berawan);
  fuzzy->addFuzzyRule(fuzzyRule27);  
}

void reconnect() 
{
  while (!client.connected()) 
  {
    Serial.print("Attempting MQTT connection...");
    String clientId = "ESP32Client-";
    clientId += String(random(0xffff), HEX);
    if (client.connect(clientId.c_str())) 
    {
      Serial.println("connected");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}
