#include <SPI.h>
#include <Adb.h>
#include <Servo.h>

#define LIGHT     0
#define SERVO     3
#define DC_SPEED  5
#define DC_CTRL1  2
#define DC_CTRL2  4

// Command definitions
#define RCV_SERVO_CTRL    0x00
#define SND_LIGHT_NTFY    0x04
#define SND_DCMOTOR_CTRL  0x05

// Adb connection.
Connection * connection;

// Servo controller
Servo servo;

// Elapsed time for ADC sampling
long lastTime;

// Event handler for the shell connection. 
void adbEventHandler(Connection * connection, adb_eventType event, uint16_t length, uint8_t * data)
{
  int i;
  Serial.print("ADB_Event: ");
  Serial.println(event);
  
  // Data packets contain two bytes, one for each servo, in the range of [0..180]
  if (event == ADB_CONNECTION_RECEIVE)
  {
    switch(data[0])
    {
      // Servo
      case RCV_SERVO_CTRL:
        Serial.print("RCV_SERVO_CTRL received param: ");
        Serial.println(data[1], DEC);
        {
          int degree = map(data[1], 0, 255, 0, 180);
          servo.write(degree);
        }
        break;

      // DC Motor
      case SND_DCMOTOR_CTRL:
        Serial.print("SND_DCMOTOR_CTRL received param: dir = ");
        Serial.print(data[1], DEC);
        Serial.print(", speed = ");
        Serial.println(data[2], DEC);
        {
          if(data[2] == 0x00)
          {
            digitalWrite(DC_CTRL1, LOW);
            digitalWrite(DC_CTRL2, LOW);
          }
          else
          {
            int speed = map(data[2], 0, 100, 0, 255);
            analogWrite(DC_SPEED, speed);
            
            if(data[1] == 0x01)
            {
              digitalWrite(DC_CTRL1, HIGH);
              digitalWrite(DC_CTRL2, LOW);
            }
            else
            {
              digitalWrite(DC_CTRL1, LOW);
              digitalWrite(DC_CTRL2, HIGH);
            }
          }
        }
        break;
        
      default:
        Serial.println("Unknown command: " + data[0]);
        break;
    }
  }
}

void setup()
{

  // Initialise serial port
  Serial.begin(57600);
  Serial.println("Hello, Arduino with MicroBridge!");
  
  // Note start time
  lastTime = millis();

  // Initialize the Servo.
  servo.attach(SERVO);
  
  // Initialize a DC motor
  pinMode(DC_SPEED, OUTPUT);
  pinMode(DC_CTRL1, OUTPUT);
  pinMode(DC_CTRL2, OUTPUT);
  digitalWrite(DC_CTRL1, LOW);
  digitalWrite(DC_CTRL2, LOW);
  
  // Initialize the ADB subsystem.  
  ADB::init();

  // Open an ADB stream to the phone's shell. Auto-reconnect
  connection = ADB::addConnection("tcp:4567", true, adbEventHandler);  
}

void sendLightSensor()
{
  uint16_t buf = analogRead(LIGHT);
  byte value = map(buf, 0, 1024, 0, 100);
  
  byte message[3];
  message[0] = SND_LIGHT_NTFY;
  message[1] = value;
  
  connection->write(2, message);
}

void loop()
{
  if(connection->isOpen()) {
    if ((millis() - lastTime) > 20)
    {
      sendLightSensor();
      lastTime = millis();
    }
  }

  // Poll the ADB subsystem.
  ADB::poll();
}

