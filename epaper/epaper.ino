
#include <Esp.h>

#include "client.h"
#include "image.h"

// Conversion factor for micro seconds to seconds
#define uS_TO_S_FACTOR 1000000

RTC_DATA_ATTR int bootCount = 0;


void setup() {

    Serial.begin(115200);
    delay(10);

    initConfig();
    // Only executed on first boot not after wakeup
    if (bootCount == 0)
    {
        Serial.println("Press Enter key to configure");
        for(int i = 0; i < 100; i++)
        {
            if(Serial.read() == '\n')
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

    displayImage(picture, pictureSize);
    free(picture);
    
    delay(3000);

    Serial.print("Sleeping ...\n");

    //sleep for one hour
    uint64_t sleeptime = UINT64_C(60 * 60 * uS_TO_S_FACTOR);
    esp_sleep_enable_timer_wakeup(sleeptime);
    esp_deep_sleep_start();
  
}

void loop() {

}
