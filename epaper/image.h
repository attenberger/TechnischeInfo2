

/* SPI pin definition --------------------------------------------------------*/
#define PIN_SPI_SCK  13
#define PIN_SPI_DIN  14
#define PIN_SPI_CS   15
#define PIN_SPI_BUSY 25//19
#define PIN_SPI_RST  26//21
#define PIN_SPI_DC   27//22

/* Pin level definition ------------------------------------------------------*/
#define LOW             0
#define HIGH            1

#define GPIO_PIN_SET   1
#define GPIO_PIN_RESET 0

void EPD_initSPI()
{
    pinMode(PIN_SPI_BUSY,  INPUT);
    pinMode(PIN_SPI_RST , OUTPUT);
    pinMode(PIN_SPI_DC  , OUTPUT);

    pinMode(PIN_SPI_SCK, OUTPUT);
    pinMode(PIN_SPI_DIN, OUTPUT);
    pinMode(PIN_SPI_CS , OUTPUT);

    digitalWrite(PIN_SPI_CS , HIGH);
    digitalWrite(PIN_SPI_SCK, LOW);
}

/* The procedure of sending a byte to e-Paper by SPI -------------------------*/
void EpdSpiTransferCallback(byte data)
{
    //SPI.beginTransaction(spi_settings);
    digitalWrite(PIN_SPI_CS, GPIO_PIN_RESET);

    for (int i = 0; i < 8; i++)
    {
        if ((data & 0x80) == 0) digitalWrite(PIN_SPI_DIN, GPIO_PIN_RESET);
        else                    digitalWrite(PIN_SPI_DIN, GPIO_PIN_SET);

        data <<= 1;
        digitalWrite(PIN_SPI_SCK, GPIO_PIN_SET);
        digitalWrite(PIN_SPI_SCK, GPIO_PIN_RESET);
    }

    //SPI.transfer(data);
    digitalWrite(PIN_SPI_CS, GPIO_PIN_SET);
    //SPI.endTransaction();
}

/* Sending a byte as a command -----------------------------------------------*/
void EPD_SendCommand(byte command)
{
    digitalWrite(PIN_SPI_DC, LOW);
    EpdSpiTransferCallback(command);
}

/* Sending a byte as a data --------------------------------------------------*/
void EPD_SendData(byte data)
{
    digitalWrite(PIN_SPI_DC, HIGH);
    EpdSpiTransferCallback(data);
}

/* Waiting the e-Paper is ready for further instructions ---------------------*/
void EPD_WaitUntilIdle()
{
    //0: busy, 1: idle
    while(digitalRead(PIN_SPI_BUSY) == 0) delay(100);
}

/* Send a one-argument command -----------------------------------------------*/
void EPD_Send_1(byte c, byte v1)
{
    EPD_SendCommand(c);
    EPD_SendData(v1);
}

/* Send a two-arguments command ----------------------------------------------*/
void EPD_Send_2(byte c, byte v1, byte v2)
{
    EPD_SendCommand(c);
    EPD_SendData(v1);
    EPD_SendData(v2);
}

/* Send a three-arguments command --------------------------------------------*/
void EPD_Send_3(byte c, byte v1, byte v2, byte v3)
{
    EPD_SendCommand(c);
    EPD_SendData(v1);
    EPD_SendData(v2);
    EPD_SendData(v3);
}

/* Send a four-arguments command ---------------------------------------------*/
void EPD_Send_4(byte c, byte v1, byte v2, byte v3, byte v4)
{
    EPD_SendCommand(c);
    EPD_SendData(v1);
    EPD_SendData(v2);
    EPD_SendData(v3);
    EPD_SendData(v4);
}

/* This function is used to 'wake up" the e-Paper from the deep sleep mode ---*/
void EPD_Reset()
{
    digitalWrite(PIN_SPI_RST, LOW);
    delay(200);

    digitalWrite(PIN_SPI_RST, HIGH);
    delay(200);
}

/* Show image and turn to deep sleep mode (7.5 and 7.5b e-Paper) -------------*/
void EPD_showC()
{
    // Refresh
    EPD_SendCommand(0x12);// DISPLAY_REFRESH
    delay(100);
    EPD_WaitUntilIdle();

    // Sleep
    EPD_SendCommand(0x02);// POWER_OFF
    EPD_WaitUntilIdle();
    EPD_Send_1(0x07, 0xA5);// DEEP_SLEEP
}

int EPD_7in5__init()
{
    EPD_Reset();
    EPD_Send_2(0x01, 0x37, 0x00);            //POWER_SETTING
    EPD_Send_2(0x00, 0xCF, 0x08);            //PANEL_SETTING
    EPD_Send_3(0x06, 0xC7, 0xCC, 0x28);      //BOOSTER_SOFT_START
    EPD_SendCommand(0x4);                    //POWER_ON
    EPD_WaitUntilIdle();
    EPD_Send_1(0x30, 0x3C);                  //PLL_CONTROL
    EPD_Send_1(0x41, 0x00);                  //TEMPERATURE_CALIBRATION
    EPD_Send_1(0x50, 0x77);                  //VCOM_AND_DATA_INTERVAL_SETTING
    EPD_Send_1(0x60, 0x22);                  //TCON_SETTING
    EPD_Send_4(0x61, 0x02, 0x80, 0x01, 0x80);//TCON_RESOLUTION
    EPD_Send_1(0x82, 0x1E);                  //VCM_DC_SETTING: decide by LUT file
    EPD_Send_1(0xE5, 0x03);                  //FLASH MODE

    EPD_SendCommand(0x10);//DATA_START_TRANSMISSION_1
    delay(2);
    return 0;
}


void displayImage(byte *imageData, size_t length) 
{
    EPD_initSPI();
    EPD_7in5__init();

    /* Each pixel in our input is represented by one bit where
    1 equals white and 0 equals black. Each pixel in our
    e-paper is represented by 4 bit where 0011 equals white
    and 0000 equals black.*/

    for (int i = 0; i < length; i++) 
    {
        /* starting at the most significant bit we convert each pair of 1-bit
        input pixels to a pair of 4-bit output pixels */
        byte position = 1 << 7;
        for (int j = 0; j < 4; j++) 
        {
            /* initialize our output to 0 which represents two black pixels */
            byte output = 0;

            /* if the left bit of our pair is set we or our output with 0x30 to
            set the corresponding pixel to white */
            if (imageData[i] & position) {
                output |= 0x30;
            }
            position >>= 1;

            /* if the right bit of our pair is set we or our output with 0x03 to
            set the corresponding pixel to white */
            if (imageData[i] & position) {
                output |= 0x03;
            }
            position >>= 1;

            EPD_SendData(output);
        }    
    }

    EPD_showC();
}
