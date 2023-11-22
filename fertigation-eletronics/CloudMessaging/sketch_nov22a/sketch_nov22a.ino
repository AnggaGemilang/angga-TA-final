#include <WiFi.h>
#include <HTTPClient.h>

HTTPClient HttpClient;

const char* SSID = "SPEEDY";
const char* PASSWORD = "suherman";

int statusnya = 0;

void setup() {
  Serial.begin(115200);
  WiFi.begin(SSID, PASSWORD);
  WiFi.setSleep(false);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
}

void sendNotification(String title, String body) {
  String DEVICE_FCM_KEY = "e0tQlw-Az2A:APA91bHtEdYptmOYWWCzEWUepfhGyq10VONGUl7ToUf91-TxWRlVUEM2ClsgE2P9GmTVGVLrnlDMNx5WY-0U4MYw7gqCr9f2MKOTWYltqC34jB8LzFd8-Pl54xwVEdxx-vCDWoyl7LPq";
  String FCM_API_KEY = "key=AAAAx7B7jBc:APA91bEL6FTL_bKgKLOFIteAL7c9iXI54Le2-D7tegps_shgzI-5c5Mqtblou5bPpQGayfYJrxhLcmrF8rZe5LqMv5rnbb2SKd71BvbStSNaaS9vfW6T1rItbIZEMtHObvAbHF55aF4X";
  String FCM_URL = "https://fcm.googleapis.com/fcm/send";

  //Check the current connection status
  if ((WiFi.status() == WL_CONNECTED)) {  
    //Specify the URL
    HttpClient.begin(FCM_URL);

    // Set headers
    HttpClient.addHeader("Authorization", FCM_API_KEY);
    HttpClient.addHeader("Content-Type", "application/json");

    // Data string
    String data = "{\"registration_ids\": [\"" + DEVICE_FCM_KEY + "\"], \"notification\": {\"body\":\"" + body + "\", \"title\":\"" + title + "\"}}";

    //Make the request
    int httpCode = HttpClient.POST(data);

    //Check for the returning code
    if (httpCode == HTTP_CODE_OK) {  
      Serial.println("Notification Sent To The Phone");
    } else {
      Serial.println("Error on sending notification");
      Serial.println(httpCode);
      Serial.println(HttpClient.getString());
    }
    HttpClient.end();  //Free the resources
  }
}

void loop() {
  if(statusnya == 0){
     sendNotification("Lorem Ipsum is simply", "Lorem Ipsum is simply dummy text of the printing and typesetting industry");
     statusnya = 1; 
  }
}
