# RXTX Examples

This project contains some examples, how the basic [RXTX dependency](https://github.com/projekt-internet-technologien/RXTX) can be used.

## Get and install

First of all, install the maven [RXTX dependency](https://github.com/projekt-internet-technologien/RXTX), following the README in that project. Make sure, that you have installed `librxtx` properly.

```
git clone https://github.com/projekt-internet-technologien/RXTX-examples.git
```

You can import this project as maven project into Eclipse.

All examples below have three possible parameters:

* `--baud` - default is `9600`, examples: `115200`
* `--ports` - defaults are `/dev/ttyUSB0` to `/dev/ttyUSB10`, `/dev/ttyACM0` (Linux) to `/dev/ttyACM1` (Linux) and `COM0` to `COM10` (Windows). Please add your own by adding the with a colon.
* `--rxtxlib` - default is `/usr/lib/jni`

## ByteExample

The ByteExample can be seen as a simple debugging class. It simply just writes the data received on the Serial (e.g., the USB) in Hex.

Make sure, that the Main-Class is configured in the `pom.xml`:

```
<Main-Class>de.dennis_boldt.example.ByteExample</Main-Class>
```

Now, build the project:

```
mvn package
```

Now attach your USB device and start the programm (from the `target` folder):

```
java -jar target/RxtxExamples-0.0.2-SNAPSHOT.jar
java -jar target/RxtxExamples-0.0.2-SNAPSHOT.jar --baud 115200
java -jar target/RxtxExamples-0.0.2-SNAPSHOT.jar --rxtxlib /usr/lib
java -jar target/RxtxExamples-0.0.2-SNAPSHOT.jar --ports /dev/ttyUSB0:/dev/ttyACM0
java -jar target/RxtxExamples-0.0.2-SNAPSHOT.jar --ports /dev/ttyUSB0:/dev/ttyACM0 --rxtxlib /usr/lib --baud 115200
```

### Arduino

To test this example, there is an example code for an Arduino, which basically prints `ABC`, followed by CR+LF, every 2,5 seconds.

Upload the `arduino/ABC/ABC.ino` to the Arduino.

## StringExample

The StringExample is similar to the ByteExamle, except it prints the result as string and you can also send data to the attached device by typing them in the console.

Make sure, that the Main-Class is configured in the `pom.xml`:

```
<Main-Class>de.dennis_boldt.example.StringExample</Main-Class>
```

Now, build the project:

```
mvn package
```

Now attach your USB device and start the programm (from the `target` folder):

```
java -jar target/RxtxExamples-0.0.2-SNAPSHOT.jar
java -jar target/RxtxExamples-0.0.2-SNAPSHOT.jar --baud 115200
java -jar target/RxtxExamples-0.0.2-SNAPSHOT.jar --rxtxlib /usr/lib
java -jar target/RxtxExamples-0.0.2-SNAPSHOT.jar --ports /dev/ttyUSB0:/dev/ttyACM0
java -jar target/RxtxExamples-0.0.2-SNAPSHOT.jar --ports /dev/ttyUSB0:/dev/ttyACM0 --rxtxlib /usr/lib --baud 115200
```

### Arduino

To test this example, there is an example code for an Arduino, which basically can turn on and off a GPIO. In the code the pin 2 is configured to control a LED.

Upload the `arduino/SerialRead/SerialRead.ino` to the Arduino.

Type `on` or `off` to the terminal. The LED should switch on and off.

# Sources

* http://angryelectron.com/rxtx-on-raspbian/
* http://eclipsesource.com/blogs/2012/10/17/serial-communication-in-java-with-raspberry-pi-and-rxtx/
* http://blog.cedarsoft.com/2010/11/setting-java-library-path-programmatically/
* http://rxtx.qbang.org/wiki/index.php/Two_way_communcation_with_the_serial_port
* http://stackoverflow.com/a/11380638/605890
