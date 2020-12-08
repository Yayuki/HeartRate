#include "LBTServer.h"
#include "LGATTServer.h"
#include "HRSensor.h"
#include "HRService.h"

HRSensor hrSensor;
HRService hrService;
uint32_t pt = 0;
uint32_t ct = 0;
boolean wait = false;

void setup()
{
  Serial.begin(115200);
  delay(2000);
  Serial.println("Start Begin");
  hrSensor.begin(onHRChange);
  LBTServer.begin((uint8_t*)"HeartRate");
  LBTServer.end();
  if (LGATTServer.begin(1, &hrService))
  {
    Serial.println("GATT Begin");
  }
  else
  {
    Serial.println("GATT Begin Failed");
  }
}

void loop()
{
  hrSensor.handleSignal();
  LGATTServer.handleEvents();
}

void onHRChange(int hrValue)
{
  hrService.sendHR(hrValue);
}
