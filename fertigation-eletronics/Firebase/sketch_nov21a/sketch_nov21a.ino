#include <WiFi.h>
#include <IOXhop_FirebaseESP32.h> 
#include <TaskScheduler.h>

#define WIFI_SSID "SPEEDY"
#define WIFI_PASSWORD "suherman"
#define HARDWARE_CODE "13kjh123kj1h3j12h21312kjhasdasd"
#define OWNER_CODE "hpoQA4Xv0hTpmsB3lgOXyrRF7S12"
#define FIREBASE_HOST "https://fertigation-system-389e8-default-rtdb.firebaseio.com/"
#define FIREBASE_AUTH "AIzaSyBZvwV5-74YkBUlphAYpuyFsHIQVyfRHW4"

Scheduler userScheduler;

String monitorDeviceData, irrigationTimes, fertigationTimes;
int fertilizerTankVal, waterTankVal, idealMoisture;
int irrigationDays, fertigationDays;
int irrigationDose, fertigationDose;
int systemInterval, userInterval;

void readControlData();

Task taskReadControlData( TASK_SECOND * 5 , TASK_FOREVER, &readControlData );

void readControlData(){
  idealMoisture = Firebase.getInt("/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/idealMoisture");
  irrigationDays = Firebase.getInt("/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/irrigationDays");
  fertigationDays = Firebase.getInt("/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/fertigationDays");
  irrigationTimes = Firebase.getString("/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/irrigationTimes");            
  fertigationTimes = Firebase.getString("/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/fertigationTimes");
  irrigationDose = Firebase.getInt("/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/irrigationDose");
  fertigationDose = Firebase.getInt("/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/parameter/13kjh123kj1h3j12h21312kjhasdasd/fertigationDose");
  systemInterval = Firebase.getInt("/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/interval/systemRequest");
  userInterval = Firebase.getInt("/controlling/hpoQA4Xv0hTpmsB3lgOXyrRF7S12/interval/userRequest");        
  Serial.printf("Get data mois=%d irrDays=%d FerDays=%d IrrTimes=%s FerTimes=%s IrrDose=%d FerDose=%d SysInt=%d UsrInt=%d\n", idealMoisture, irrigationDays, fertigationDays, irrigationTimes, fertigationTimes, irrigationDose, fertigationDose, systemInterval, userInterval);
    
  taskReadControlData.setInterval((TASK_SECOND * 5));    
}

void setup() {
  Serial.begin(115200);  
  while (!Serial);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);                                  
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID);
  while (WiFi.status() != WL_CONNECTED) 
  {
    Serial.print(".");
    delay(500);
  }
 
  Serial.println();
  Serial.print("Connected");
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());
  
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Serial.print("Firebase Connected");

  userScheduler.addTask(taskReadControlData);
  taskReadControlData.enable();
}


void loop() {
  userScheduler.execute();
}
