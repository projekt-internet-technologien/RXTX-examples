void setup() {
     Serial.begin(115200);
}

void loop() {
  // 0x41  0x42  0x43  0x0d  0x0a
  //    A     B     C  (CR)  (LF)
  Serial.println("ABC"); 
  delay(2500);
}
