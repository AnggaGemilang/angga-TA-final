#include <HardwareSerial.h>

HardwareSerial SerialPort(2);

void setup()
{
  Serial.begin(115200);
  SerialPort.begin(9600, SERIAL_8N1, 16, 17);
}
void loop()
{
  if (SerialPort.available())
  {
    String sabihis = String(SerialPort.readString());
    Serial.println("Terima " + sabihis);
  }
}
