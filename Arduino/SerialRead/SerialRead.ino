// @see: http://stackoverflow.com/a/17051931/605890
String inData;

int ledPin = 2; 

void setup() {
    Serial.begin(9600);
    pinMode(ledPin, OUTPUT);
}

void loop() {
    while (Serial.available() > 0)
    {
        char recieved = Serial.read();
        inData += recieved; 
        if (recieved == '\n')
        {
            Serial.print("Arduino Received: ");
            Serial.print(inData);
            
            inData.trim();
            
            if (String("on").equals(inData)) {
               digitalWrite(ledPin, HIGH);
            } else if (String("off").equals(inData)) {
               digitalWrite(ledPin, LOW);
            } else {
                Serial.print("Unknown command");
            }
            
            inData = ""; // Clear recieved buffer
        }
    }
}
