#include "HRService.h"

static LGATTServiceInfo g_hr_decl[] = {
  {TYPE_SERVICE, "0000180D-0000-1000-8000-00805f9b34fb", TRUE, 0, 0, 0},
  {TYPE_CHARACTERISTIC, "00002A37-0000-1000-8000-00805f9b34fb", FALSE, VM_GATT_CHAR_PROP_NOTIFY, 0, 0},
  {TYPE_DESCRIPTOR, "00002902-0000-1000-8000-00805f9b34fb", FALSE, VM_GATT_CHAR_PROP_READ | VM_GATT_CHAR_PROP_WRITE, VM_GATT_PERM_READ | VM_GATT_PERM_WRITE, 0},
  {TYPE_END, 0, 0, 0, 0, 0}
};

HRService::HRService()
{
  _isConnect = false;
  flags.hrvf = 0;
  flags.scs = 0;
  flags.ees = 0;
  flags.rri = 0;
  prop.notify = 0;
  prop.indic = 0;
}

LGATTServiceInfo *HRService::onLoadService(int32_t index)
{
  return g_hr_decl;
}

boolean HRService::onCharacteristicAdded(LGATTAttributeData &data)
{
  const VM_BT_UUID *uuid = &(data.uuid);
  if (!data.failed)
  {
    if (0x37 == uuid->uuid[12])
    {
      _hrmHandle = data.handle;
    }
  }
  Serial.printf("%x,%x\n", uuid->uuid[13], uuid->uuid[12]);
  return true;
}

boolean HRService::onDescriptorAdded(LGATTAttributeData &data)
{
  const VM_BT_UUID *uuid = &(data.uuid);
  if (!data.failed)
  {
    if (0x02 == uuid->uuid[12])
    {
      _cccHandle = data.handle;
    }
  }
  Serial.printf("%x,%x\n", uuid->uuid[13], uuid->uuid[12]);
  return true;
}

boolean HRService::onConnection(const LGATTAddress &addr, boolean isConnect)
{
  Serial.printf("onConnection : %d\n", isConnect);
  _isConnect = isConnect;
  if (!isConnect)
    prop.notify = isConnect;
  return true;
}

boolean HRService::onRead(LGATTReadRequest &data)
{
  Serial.printf("onRead Handle : %d\n", data.attr_handle);
  if (_isConnect && data.attr_handle == _cccHandle)
  {
    LGATTAttributeValue value = {0};
    value.value[0] = prop.i;
    value.len = 1;
    data.ackOK(value);
  }
  return true;
}

boolean HRService::onWrite(LGATTWriteRequest &data)
{
  Serial.printf("onWrite Handle : %d\n", data.attr_handle);
  Serial.printf("onWrite rx[%x][%x]\n", data.value.value[0], data.value.value[1]);
  if (_isConnect && data.attr_handle == _cccHandle)
  {
    if (data.value.value[0] == 0)
    {
      prop.notify = 0;
    }
    else if (data.value.value[0] == 1)
    {
      prop.notify = 1;
    }
    if (data.need_rsp)
      data.ackOK();
  }
  return true;
}

void HRService::sendHR(int hrValue)
{
  if (!_isConnect || !prop.notify)
    return;
  LGATTAttributeValue value = {0};
  value.value[0] = flags.i;
  value.value[1] = hrValue;
  value.len = 2;
  sendIndication(value, _hrmHandle, false);
}
