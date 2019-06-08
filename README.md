# E-Paper Display für das HM ZPA

Das E-Paper Display zeigt die Belegung für einen spezifischen Raum für einen Wochentag an.

### Beispiel

![](https://github.com/attenberger/TechnischeInfo2/blob/master/examplescreen.jpg)

### Projektstruktur & grobe Erklärung

- Sourcecode für das E-Paper Driver Board im Verzeichnis ``epaper``
  - ``epaper.ino``: Enthält die für Arduino typischen ``setup()`` und ``loop()`` Methoden und ist der Einstiegspunkt der Anwendung
  - ``config.h``: Ist für das Laden und Speichern der Konfiguration auf dem Driver Board zuständig
  - ``client.h``: Ist für die Verbindung mit dem WLAN und Server zuständig
  - ``image.h``: Ist für die Ansteuerung des E-Papers zuständig. Ein Großteil des Codes stammt dabei aus einer [Waveshare Demo](https://www.waveshare.com/wiki/File:E-Paper_ESP32_Driver_Board_Code.7z)
  
  #### Ablauf
  - Beim Ersten Start wartet das Driver Board für 10 Sekunden auf eine mögliche Konfiguration über die Serielle Schnittstelle
  - Danach verbindet sich das E-Paper Driver Board mit dem WLAN
  - Anschließend wird eine TCP-Verbindung mit dem Server aufgebaut
  - Die Raumnummer wird an den Server geschickt
  - Das Bild wird vom Server empfangen und dargestellt
  - Das Board geht für eine Stunde schlafen und startet danach von vorne
- Sourcecode für den Server in ``src/main/java/techInf2/server``
  - Das Driver Board schickt über TCP die Raumnummer z.B. "R1.010A" an den Server
  - Der Server fragt dann automatisch das ZPA an, parst den HTML-Baum vom ZPA und generiert daraus das Bild für das E-Paper
  - Das Bild wird per TCP in Bytes kodiert an das Driver Board geschickt
  - Ein Bit entspricht dabei einem Pixel (schwarz / weiß)
  - Anzahl verschickter Bytes: 384px * 640px = 245760 Bits = 30720 Bytes

### Dependency (für den Server)

- [https://github.com/jhy/jsoup](https://github.com/jhy/jsoup)

### Hardware

- [E-Paper ESP32 Driver Board](https://www.waveshare.com/wiki/E-Paper_ESP32_Driver_Board)
- [7.5inch E-Paper](https://www.waveshare.com/wiki/7.5inch_e-Paper_HAT)

### Stromverbrauch

- aktiver Zustand: ca. 70mA, dauer ca. 16 Sekunden
- Deep Sleep: 2,7mA wenn die Power-LED abgelötet wird, ansonsten 4,2mA

Dadurch ergibt sich bei einer Aktualisierung pro Stunde ein durchschnittlicher Verbrauch von 3mA.

### Installation und Konfiguration des E-Paper Driver Boards

#### Installation
1. Download des Quellcodes aus diesem Repository
2. Anschließen des E-Papers an das Driver Board
3. Den Schalter auf dem Driver Board auf Position B setzen
4. Anschließen des Driver Boards per USB an den Rechner
5. Die [Arduino IDE](https://www.arduino.cc/en/Main/Software) installieren
6. [Arduino Core for ESP32](https://github.com/espressif/arduino-esp32) installieren
7. Das Projekt in der Arduino IDE öffnen, ``ESP32 Dev Module`` als Board und die passende Serielle Schnittstelle auswählen
8. Auf ``Upload`` klicken, um den Code zu kompilieren und auf das Driver Board zu übertragen

#### Konfiguration
Das Driver Board kann über die Serielle Schnittstelle konfiguriert werden. Dazu muss nach einem Reset (zum Beispiel durch Drücken des EN-Buttons auf dem Board) innerhalb von 10 Sekunden die Enter-Taste gedrückt werden. Anschließend können SSID und Passwort des WLANs, IP-Adresse und Port des Servers und die Raumnummer eingegeben werden.

Wichtig: Falls Putty statt des Serial Monitors der Arduino IDE zum Konfigurieren genutzt wird funktioniert das Drücken der Enter-Taste nicht, da Putty nur ein ``\r`` sendet. Dort muss statt der Enter-Taste STRG-J zum Senden von ``\n`` verwendet werden.

### Starten des Servers

- Der Server lässt sich mit Maven starten: ``TODO``

### Testen des Servers

- Es wurde ein Testclient geschrieben, der das ESP Board simuliert.
- File: ``Test.java``
- main starten, dann wird das übertragene Bild als ``testgenerated.png`` gespeichert. Das Bild ist gespiegelt, hier könnte der Testclient noch verbessert werden... :-)
- vorher nicht vergessen, den Server zu starten

