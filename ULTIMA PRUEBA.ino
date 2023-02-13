#include<SoftwareSerial.h>// Libreria para transmision Bluetooth
SoftwareSerial BTSerial(7, 6);//pines de transmision Bluetooth
void setup  ()
{
  Serial.begin(9600);// Velocidad de transmision por puerto serie.
  BTSerial.begin(9600);// Velocidad de transmision por bluetooth.
  pinMode(10, INPUT); // Configuración para detección de derivaciones LO +.
  pinMode(11, INPUT);// Configuración para detección de derivaciones LO -.
}
void loop() {
  if(BTSerial.available()){
    char Mensaje = BTSerial.read();
    if( Mensaje == 'A'){
      for(int i=0; i<4500; i++){
        if((digitalRead(10) == 1)||(digitalRead(11) == 1))
        {
        Serial.println('0');
        BTSerial.println('0');
        }
        else
        {// enviar el valor de la entrada analógica 1
        Serial.println(analogRead(A1));
        BTSerial.println(analogRead(A1));
        }
        delay(100); // Tiempo de espera para evitar que los datos en serie se saturen
        if( BTSerial.read() == 'Z'){
        break;
        }
      }
    }
  }
}  
    