#include <Firebase_ESP_Client.h>
#include <TaskScheduler.h>
#include <WiFi.h>

// #define WIFI_SSID "SPEEDY"
// #define WIFI_PASSWORD "suherman"
#define WIFI_SSID "Galaxy M33 5G"
#define WIFI_PASSWORD "anggaganteng"
#define SOIL_MOISTURE_PIN A0
#define WATER_LEVEL_PIN A3
#define DATABASE_URL "https://fertigation-system-389e8-default-rtdb.firebaseio.com/"
#define API_KEY "AIzaSyBZvwV5-74YkBUlphAYpuyFsHIQVyfRHW4"

Scheduler userScheduler;

int interval, moistureVal, waterLevelVal;
String BASE_URL = "/monitoring/2X0JeST2hUe8K5qLpk5f6gCC0zO2/13kjh123kj1h3j12h21312kjhasdasd/monitorDevice/1/";

int soilMoisture();
int waterLevel();
void sendMessage();

Task taskSendMessage(interval, TASK_FOREVER, &sendMessage);
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

void sendMessage() {
  moistureVal = soilMoisture();
  waterLevelVal = waterLevel();
  Firebase.RTDB.setInt(&fbdo, BASE_URL + "moisture", moistureVal);
  Firebase.RTDB.setInt(&fbdo, BASE_URL + "waterLevel", waterLevelVal);
  Serial.printf("Upload to monitor device msgMois=%d msgWater=%d\n", moistureVal, waterLevelVal);

  if(Firebase.RTDB.getInt(&fbdo, "/controlling/2X0JeST2hUe8K5qLpk5f6gCC0zO2/interval/userRequest")){
    if(fbdo.dataType() == "int"){
      interval = fbdo.intData();
      Serial.printf("Get data interval=%d\n", interval);
    }
  }
  
  taskSendMessage.setInterval(interval);
}

void setup() {
  Serial.begin(115200);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);                                  
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID);
  while (WiFi.status() != WL_CONNECTED) 
  {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;

  if (Firebase.signUp(&config, &auth, "", "")){
    Serial.println("Firebase Connected");
  }
  else{
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }
  
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  if(Firebase.RTDB.getInt(&fbdo, "/controlling/2X0JeST2hUe8K5qLpk5f6gCC0zO2/interval/userRequest")){
    if(fbdo.dataType() == "int"){
      interval = fbdo.intData();
      Serial.printf("Get data interval=%d\n", interval);
    }
  }
  
  userScheduler.init();
  userScheduler.addTask(taskSendMessage);
  taskSendMessage.enable();
}

void loop() {
  userScheduler.execute();
  interval = 5000;
}
