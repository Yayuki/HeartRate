#include "LGATTServer.h"

class HRService : public LGATTService
{
public:
  HRService();
  boolean isConnected()
  {
    return _isConnect;
  };
  virtual void sendHR(int hrValue);

protected:
  virtual LGATTServiceInfo *onLoadService(int32_t index);
  virtual boolean onCharacteristicAdded(LGATTAttributeData &data);
  virtual boolean onDescriptorAdded(LGATTAttributeData &data);
  virtual boolean onConnection(const LGATTAddress &addr, boolean connected);
  virtual boolean onRead(LGATTReadRequest &data);
  virtual boolean onWrite(LGATTWriteRequest &data);

private:
  uint16_t _hrmHandle;
  uint16_t _cccHandle;
  boolean _isConnect;
  union
  {
    uint8_t i;
    struct
    {
      unsigned hrvf : 1;
      unsigned scs : 2;
      unsigned ees : 1;
      unsigned rri : 1;
    };
  } flags;
  union
  {
    uint8_t i;
    struct
    {
      unsigned notify : 1;
      unsigned indic : 1;
    };
  } prop;
};
