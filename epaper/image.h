
#include "srvr.h"


void displayImage(byte *imageData, size_t length) 
{
    Serial.println("Entering displayImage");
    EPD_initSPI();
    EPD_dispIndex = 19;
    EPD_dispInit();
    Serial.println("Length is:");
    Serial.println(length);
    for (int i = 0; i < length; i++) 
    {
        byte position = 1 << 7;
        for (int j = 0; j < 4; j++) 
        {
            byte output = 0;
            if (imageData[i] & position) {
                output |= 0x30;
            }
            position >>= 1;
            if (imageData[i] & position) {
                output |= 0x03;
            }
            position >>= 1;
            EPD_SendData(output);
        }    
    }

    EPD_showC();
}
