# E-Paper Display für das HM ZPA

Das E-Paper Display zeigt die Belegung für einen spezifischen Raum für einen Wochentag an.

### Beispiel

![](https://github.com/attenberger/TechnischeInfo2/blob/master/examplescreen.jpg)

### Projektstruktur & grobe Erklärung

- Sourcecode für das ESP Driver Board im Verzeichnis ``epaper``
  - Es wird ein zyklischer Deepsleep von TODO x Sekunden ausgeführt. Bei jedem Aufwachen wird der Server angefragt.
- Sourcecode für den Server in ``TODO``
  - Der ESP schickt über TCP die Raumnummer z.B. "R1.010A" an den Server
  - Der Server fragt dann automatisch das ZPA an, parst den HTML-Baum vom ZPA und generiert daraus das Bild für das E-Paper
  - Das Bild wird per TCP in Bytes kodiert an den ESP geschickt
  - Ein Bit entspricht dabei einem Pixel (schwarz / weiß)
  - Anzahl verschickter Bytes: 384px * 640px = 245760 Bits = 30720 Bytes

### Hardware

- [E-Paper ESP32 Driver Board](https://www.waveshare.com/wiki/E-Paper_ESP32_Driver_Board)
- [7.5inch E-Paper](https://www.waveshare.com/wiki/7.5inch_e-Paper_HAT)

### Stromverbrauch

- aktiver Zustand: ca. 70mA, dauer ca. 16 Sekunden
- Deep Sleep: 2,7mA

### Konfiguration und Starten des ESP Boards

- TODO: welche Entwicklungsumgebung, wlan-pw und ssid, server ip, compile code, code flashen, wie board starten (welchen Button drücken), ...

### Starten des Servers

- Der Server lässt sich mit Maven starten: ``TODO``

### Testen des Servers

- Es wurde ein Testclient geschrieben, der das ESP Board simuliert.
- File: ``Test.java``
- main Starten, dann wird das übertragene Bild als ``testgenerated.png`` gespeichert. Das Bild ist gespiegelt, hier könnte der Testclient noch verbessert werden... :-)
- vorher nicht vergessen, den Server zu starten