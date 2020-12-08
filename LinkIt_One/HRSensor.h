#include "Arduino.h"

typedef void(* OnHRChange)(int hrValue);

class HRSensor {
  public:
    HRSensor();
    void begin(OnHRChange);
    void handleSignal();
  private:
    static const int PULSE_PIN = A0;
    OnHRChange _onHRChange;
    int _hr;
    int _rate[10];
    unsigned long sampleCounter;
    unsigned long lastBeatTime;
    int _peek;
    int _trough;
    int _thresh;
    int _rri;
    int _amp;
    boolean firstBeat;
    boolean secondBeat;
    boolean _pulse;
    int _signal;
};
