#include <EEPROM.h>


char c_ssid[100] = {0};
char c_pass[100] = {0};
char c_ip[100] = {0};
uint16_t c_port = 12345;
uint16_t c_roomID = 0;






void showHelp();
void configure();
char readEcho();
bool parseLine();
bool parseArgument(char *buf);
void setOption(char *option, char *value);
void readOption(char *option);
void initConfig();


void initConfig()
{
    EEPROM.begin(500);
}


void configure()
{
    Serial.println("Configure mode entered");
    showHelp();
    while(parseLine());
}


void showHelp()
{
    
    Serial.println("Available commands:");
    Serial.println("\thelp");
    Serial.println("\t\tShow help screen\n");
    Serial.println("\tset");
    Serial.println("\t\tSet new setting\n");
    Serial.println("\tread");
    Serial.println("\t\tRead settings value\n");
    Serial.println("\tend");
    Serial.println("\t\tEnd configure mode\n");
    Serial.println("Available options:");
    Serial.println("\tssid");
    Serial.println("\tpass");
    Serial.println("\tipaddress");
    Serial.println("\tport");
    Serial.println("\troomID");
    
}


bool parseLine()
{
    Serial.print("# ");
    
    char command[100] = {0};
    char option[100] = {0};
    char value[100] = {0};


    if(parseArgument(command))
        goto evaluate;
    if(parseArgument(option))
        goto evaluate;
    if(parseArgument(value))
        goto evaluate;
    

    while(true)
    {
        char c = readEcho();
        if (c == 255)
        {
            delay(50);
            continue;
        }
        else if(c == '\n')
            goto evaluate;
    }


    evaluate:
    if(strcmp(command, "help") == 0)
        showHelp();
    if(strcmp(command, "set") == 0)
        setOption(option, value);
    if(strcmp(command, "read") == 0)
        readOption(option);
    if(strcmp(command, "end") == 0)
        return false;
    
    return true;
    
}

bool parseArgument(char *buf)
{
    char c;
    unsigned long i = 0;
    while(true)
    {
        c = readEcho();
        if(c == ' ')
            return false;
        else if(c == '\n')
            return true;
        else if(c == 255)
        {
            delay(50);
            continue;
        }
        buf[i % 100] = c;
        i++;
    }   

}


char readEcho()
{
    if(!Serial.available())
        return 255;
    char c = Serial.read();
    Serial.print(c);
    return c;
    
}


void setOption(char *option, char *value)
{
    if(strcmp(option, "ssid") == 0)
    {
        EEPROM.writeBytes(0, value, strlen(value) + 1);
        EEPROM.commit();
    }
    else if(strcmp(option, "pass") == 0)
    {
        EEPROM.writeBytes(100, value, strlen(value) + 1);
        EEPROM.commit();
    }
    else if(strcmp(option, "ipaddress") == 0)
    {
        EEPROM.writeBytes(200, value, strlen(value) + 1);
        EEPROM.commit();
    }
    else if(strcmp(option, "port") == 0)
    {
        int32_t x = atoi(value);
        EEPROM.writeInt(300, x);
        EEPROM.commit();
    }
    else if(strcmp(option, "roomID") == 0)
    {
        int32_t x = atoi(value);
        EEPROM.writeInt(304, x);
        EEPROM.commit();
    }
    else
    {
        Serial.println("Unknown option");
        return;
    }
    Serial.print("Set ");
    Serial.print(option);
    Serial.print(" to ");
    Serial.println(value);
}
void readOption(char *option)
{
    char buff[100];
    if(strcmp(option, "ssid") == 0)
    {
        EEPROM.readBytes(0, buff, 100);
        Serial.print(option);
        Serial.print(": ");
        Serial.println(buff);
    }
    else if(strcmp(option, "pass") == 0)
    {
        EEPROM.readBytes(100, buff, 100);
        Serial.print(option);
        Serial.print(": ");
        Serial.println(buff);
    }
    else if(strcmp(option, "ipaddress") == 0)
    {
        EEPROM.readBytes(200, buff, 100);
        Serial.print(option);
        Serial.print(": ");
        Serial.println(buff);
    }
    else if(strcmp(option, "port") == 0)
    {
        int32_t x = EEPROM.readInt(300);
        Serial.print(option);
        Serial.print(": ");
        Serial.println(x, DEC);
    }
    else if(strcmp(option, "roomID") == 0)
    {
        int32_t x = EEPROM.readInt(304);
        Serial.print(option);
        Serial.print(": ");
        Serial.println(x, DEC);
    }
    else
    {
        Serial.println("Unknown option");
        return;
    }
}
