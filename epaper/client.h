
#include <WiFi.h>

const char* ssid     = "Hotspot_CB";
const char* password = "zxpt4402";
const char* host = "192.168.43.111";
const uint16_t port = 5555;
const uint16_t roomID = 0;

const size_t pictureSize = 30 * 1024;

void setupWIFI()
{
    
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
    Serial.println("WiFi connected");
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());
    
}

void getPicture(uint8_t *buf)
{
    WiFiClient client;
    Serial.print("Connecting to ");
    Serial.println(host);

    while(!client.connect(host, port)) {
        Serial.println("Connection failed.");
        Serial.println("Waiting 5 seconds before retrying...");
        delay(5000);
    }

    client.write("R2.007\n");
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
        //int x = client.read(buf, pictureSize);
//        Serial.println(x,DEC);
    /*while(i < pictureSize)
    {
        uint8_t x = client.read();
        buf[i] = x;
        i++;
    }*/
    
}
    
