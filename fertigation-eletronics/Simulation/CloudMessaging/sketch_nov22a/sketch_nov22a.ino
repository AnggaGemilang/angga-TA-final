#include <WiFi.h>
#include <HTTPClient.h>

HTTPClient HttpClient;

// #define WIFI_SSID "SPEEDY"
// #define WIFI_PASSWORD "suherman"
#define WIFI_SSID "Galaxy M33 5G"
#define WIFI_PASSWORD "anggaganteng"

int statusnya = 0;

void setup() {
  Serial.begin(115200);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  WiFi.setSleep(false);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
}

void sendNotification(String title, String body) {
  String DEVICE_FCM_KEY = "euYSAs3taVg:APA91bFo6BZdV6c3XHN3TJrxTCIDz8WG1X2P9PL6HzrQ1v9YcToLKE27ScZMxOsDMMwMVOVEZfG4mt7HnED4X-uSjoLG2FI0YVWuMRdUzkzv_gOvJpiJPqKrnNP0iLI0uTCCr-2-MWYD";
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
//  if(statusnya == 0){
//     sendNotification("Lorem Ipsum is simply", "Lorem Ipsum is simply dummy text of the printing and typesetting industry");
//     statusnya = 1; 
//  }

//  sendNotification("Water doesn't reach the ideal high limit", "Immediately check for possible damage to the pump");
//  sendNotification("Soil moisture level has reached the ideal limit", "The irrigation activity is skipped");
  sendNotification("There is a time difference of more than 1 minute", "Immediately check for possible damage to the RTC sensor");
  delay(200000);
  
}
