# LW003 Android SDK Instruction DOC（English）

----

## 1. Import project

**1.1 Import "Module mokosupport" to root directory**

**1.2 Edit "settings.gradle" file**

```
include ':app', ':mokosupport'
```

**1.3 Edit "build.gradle" file under the APP project**


	dependencies {
		...
		implementation project(path: ':mokosupport')
	}


----

## 2. How to use

**Initialize sdk at project initialization**

```
LoRaLW003MokoSupport.getInstance().init(getApplicationContext());
```

**SDK provides three main functions:**

* Scan the device;
* Connect to the device;
* Send and receive data.

### 2.1 Scan the device

 **Start scanning**

```
mokoBleScanner.startScanDevice(callback);
```

 **End scanning**

```
mokoBleScanner.stopScanDevice();
```
 **Implement the scanning callback interface**

```java
/**
 * @ClassPath com.moko.support.lw003.callback.MokoScanDeviceCallback
 */
public interface MokoScanDeviceCallback {
    void onStartScan();

    void onScanDevice(DeviceInfo device);

    void onStopScan();
}
```
* **Analysis `DeviceInfo` ; inferred `BeaconInfo`**

```
BeaconInfo beaconInfo = new BeaconInfoParseableImpl().parseDeviceInfo(device);
```

Device types can be distinguished by `parseDeviceInfo(DeviceInfo deviceInfo)`.Refer `deviceInfo.scanResult.getScanRecord().getServiceData()` we can get parcelUuid,etc.

```
        Iterator iterator = map.keySet().iterator();
        if (iterator.hasNext()) {
            ParcelUuid parcelUuid = (ParcelUuid) iterator.next();
            if (parcelUuid.toString().startsWith("0000aa00")) {
                byte[] bytes = map.get(parcelUuid);
                if (bytes != null) {
                    deviceType = bytes[0] & 0xFF;
                }
            } else {
                return null;
            }
        }

```

### 2.2 Connect to the device


```
LoRaLW003MokoSupport.getInstance().connDevice(address);
```

When connecting to the device, context, MAC address and callback by EventBus.


```
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
        String action = event.getAction();
        if (MokoConstants.ACTION_DISCONNECTED.equals(action)) {
            ...
        }
        if (MokoConstants.ACTION_DISCOVER_SUCCESS.equals(action)) {
            ...
        }
    }
```

It uses `EventBus` to notify activity after receiving the status.

### 2.3 Send and receive data.

All the request data is encapsulated into **TASK**, and sent to the device in a **QUEUE** way.
SDK gets task status from task callback (`OrderTaskResponse`) after sending tasks successfully.

* **Task**

At present, all the tasks sent from the SDK can be divided into 4 types:

> 1.  READ：Readable
> 2.  WRITE：Writable
> 3.  WRITE_NO_RESPONSE：After enabling the notification property, send data to the device and listen to the data returned by device.

Encapsulated tasks are as follows:

Custom device information
--

|Task Class|Task Type|Function
|----|----|----
|`GetManufacturerNameTask`|READ|Get manufacturer.
|`GetModelNumberTask`|READ|Get model number.
|`GetHardwareRevisionTask`|READ|Get hardware version.
|`GetFirmwareRevisionTask`|READ|Get firmware version.
|`GetSoftwareRevisionTask`|READ|Get software version.

Adv iBeacon information
--

|Task Class|Task Type|Function
|----|----|----
|`ParamsTask`|RESPONSE_TYPE_WRITE_NO_RESPONSE|Get or set params.

...

* **Create tasks**

Examples of creating tasks are as follows:

```
            List<OrderTask> orderTasks = new ArrayList<>();
            // sync time after connect success;
            orderTasks.add(OrderTaskAssembler.setTime());
            // get lora params
            orderTasks.add(OrderTaskAssembler.getLoraRegion());
            orderTasks.add(OrderTaskAssembler.getLoraMode());
            orderTasks.add(OrderTaskAssembler.getLoraClassType());
            orderTasks.add(OrderTaskAssembler.getLoRaConnectable());
            orderTasks.add(OrderTaskAssembler.getMulticastEnable());
            orderTasks.add(OrderTaskAssembler.getTimeSyncInterval());
            LoRaLW003MokoSupport.getInstance().sendOrder(orderTasks.toArray(new OrderTask[]{}));
    
```

* **Send tasks**

```
LoRaLW003MokoSupport.getInstance().sendOrder(OrderTask... orderTasks);
```

The task can be one or more.

* **Task callback**


```java
	@Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
        final String action = event.getAction();
        if (MokoConstants.ACTION_ORDER_TIMEOUT.equals(action)) {
        }
        if (MokoConstants.ACTION_ORDER_FINISH.equals(action)) {
        }
        if (MokoConstants.ACTION_ORDER_RESULT.equals(action)) {
        }
    }
   
```

`ACTION_ORDER_RESULT`

	After the task is sent to the device, the data returned by the device can be obtained by using the `onOrderResult` function, and you can determine witch class the task is according to the `response.orderType` function. The `response.responseValue` is the returned data.

`ACTION_ORDER_TIMEOUT`

	Every task has a default timeout of 3 seconds to prevent the device from failing to return data due to a fault and the fail will cause other tasks in the queue can not execute normally. After the timeout, the `onOrderTimeout` will be called back. You can determine witch class the task is according to the `response.orderType` function and then the next task continues.

`ACTION_ORDER_FINISH`

	When the task in the queue is empty, `onOrderFinish` will be called back.

* **Listening task**

When there is data returned from the device, the data will be sent in the form of broadcast, and the action of receiving broadcast is `MokoConstants.ACTION_CURRENT_DATA`.

```
String action = intent.getAction();
...
if (MokoConstants.ACTION_CURRENT_DATA.equals(action)) {
    OrderTaskResponse response = event.getResponse();
    OrderCHAR orderCHAR = (OrderCHAR) response.orderCHAR;
    int responseType = response.responseType;
    byte[] value = response.responseValue;
    ...
}
```

Get `OrderTaskResponse` from the `OrderTaskResponseEvent`, and the corresponding **key** value is `response.responseValue`.

## 3. Special instructions

> 1. AndroidManifest.xml of SDK has declared to access SD card and get Bluetooth permissions.
> 2. The SDK comes with logging, and if you want to view the log in the SD card, please to use "XLog". The log path is : root directory of SD card/LW003/LW003.txt. It only records the log of the day and the day before.















