int pinSensor = A0;  
int val = 0; 

void setup()
{
  Serial.begin(9600);
}

void loop()
{  
  val = analogRead(pinSensor);
  Serial.println(val);
  delay(500);
}
