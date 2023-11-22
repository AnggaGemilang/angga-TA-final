#include <HardwareSerial.h>

HardwareSerial SerialPort(2); // use UART2

void setup()  
{
  Serial.begin(115200);
  SerialPort.begin(9600, SERIAL_8N1, 16, 17); 
} 
void loop()  
{
  String dadang = "{sadasdasdas}asdasdasdasdasdasdasdasdasdasdasdadsadasdasdasd";
  Serial.println(dadang);
  SerialPort.print(dadang);
  delay(5000);
}
