
#include <WiFi.h>
#include "config.h"


const size_t pictureSize = 30 * 1024;

void setupWIFI()
{
    char ssid[100] = {0};
    char password[100] = {0};
    getSSID(ssid);
    getPass(password);
    
    Serial.println();
    Serial.println();
    Serial.print("Connecting to ");
    Serial.println(ssid);
    

    // Applying SSID and password
    WiFi.begin(ssid, password); 

    // Waiting the connection to a router
    while (WiFi.status() != WL_CONNECTED) 
    {
        delay(500);
        Serial.print(".");
    }

    // Connection is complete
    Serial.println("");

    Serial.println("WiFi connected");
    Serial.println("");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());
    
}

void getPicture(uint8_t *buf)
{
    char host[100] = {0};
    char room[100] = {0};
    int32_t tmp;
    uint16_t port;
    getIP(host);
    getRoom(room);
    getPort(&tmp);
    port = tmp;
    
    WiFiClient client;
    Serial.print("Connecting to ");
    Serial.println(host);

    while(!client.connect(host, port)) {
        Serial.println("Connection failed.");
        Serial.println("Waiting 5 seconds before retrying...");
        delay(5000);
    }

    client.write(room);
    client.write("\n");
    client.flush();

    int i = 0;

    while(!client.available())delay(1);
    delay(100);
    for (int i = 0; i < pictureSize; ){
        int x = client.read(&buf[i], pictureSize);
        if ( x != -1) 
            i+= x;
        else
            delay(100);
    }
    
}
    
