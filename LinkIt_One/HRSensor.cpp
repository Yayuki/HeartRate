#include "HRSensor.h"

HRSensor::HRSensor() {
  sampleCounter = 0;
  lastBeatTime = 0;
  _peek = 512;
  _trough = 512;
  _thresh = 525;
  _rri = 600;
  _amp = 100;
  firstBeat = true;
  secondBeat = false;
  _pulse = false;
  _signal = 0;
}

void HRSensor::begin(OnHRChange callback) {
  _onHRChange = callback;
}

void HRSensor::handleSignal() {
  int s = millis();
  _signal = analogRead(PULSE_PIN);
  sampleCounter += millis() - s;
  int N = sampleCounter - lastBeatTime;
  //Serial.printf("Signal: %d\n", _signal);
  if (_signal < _thresh && N > (_rri / 5) * 3) {
    if (_signal < _trough) {
      _trough = _signal;
    }
  }

  if (_signal > _thresh && _signal > _peek) {
    _peek = _signal;
  }

  if (N > 250) {
    if ( (_signal > _thresh) && (_pulse == false) && (N > (_rri / 5) * 3) ) {
      _pulse = true;
      _rri = sampleCounter - lastBeatTime;
      lastBeatTime = sampleCounter;

      if (secondBeat) {
        secondBeat = false;
        for (int i = 0; i <= 9; i++) {
          _rate[i] = _rri;
        }
      }

      if (firstBeat) {
        firstBeat = false;
        secondBeat = true;
        return;
      }

      word runningTotal = 0;

      for (int i = 0; i <= 8; i++) {
        _rate[i] = _rate[i + 1];
        runningTotal += _rate[i];
      }

      _rate[9] = _rri;
      runningTotal += _rate[9];
      runningTotal /= 10;
      _hr = 60000 / runningTotal;
      _onHRChange(_hr);
    }
  }

  if (_signal < _thresh && _pulse == true) {
    _pulse = false;
    _amp = _peek - _trough;
    _thresh = _amp / 2 + _trough;
    _peek = _thresh;
    _trough = _thresh;
  }

  if (N > 2500) {
    _thresh = 512;
    _peek = 512;
    _trough = 512;
    lastBeatTime = sampleCounter;
    firstBeat = true;
    secondBeat = false;
  }
}
