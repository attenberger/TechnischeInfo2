
#define uS_TO_S_FACTOR 1000000  /* Conversion factor for micro seconds to seconds */
#include <Esp.h>

#include "client.h"
#include "config.h"
#include "image.h"

RTC_DATA_ATTR int bootCount = 0;


void setup() {

    Serial.begin(115200);
    delay(10);

    initConfig();
    // Only executed on first boot not after wakeup
    if (bootCount == 0)
    {
        Serial.println("Press Enter key to configure");
        for(int i = 0; i < 1; i++)
        {
            if(Serial.read() == '\n' )
            {
                configure();
                break;
            }
            delay(100);
        }
        Serial.println("No config received. Starting normal process");
    }
    else
        Serial.println("Wakeup...");
    bootCount++;

    setupWIFI();
    uint8_t *picture = (uint8_t *)malloc(pictureSize);
    getPicture(picture);

    
    
    for(int i = 0; i < pictureSize; i++)
    {
        Serial.print(picture[i], HEX);
        Serial.print(" ");
        if(i%128 == 127)
            Serial.println();
    }
    

    displayImage(picture, pictureSize);
    free(picture);
    
 
  




  /*delay(3000);

  Serial.print("End\n");

  esp_sleep_enable_timer_wakeup(TIME_TO_SLEEP * uS_TO_S_FACTOR);
  esp_deep_sleep_start();
  */
}

void loop() {

}
