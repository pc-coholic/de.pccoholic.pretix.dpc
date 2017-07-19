# <img src="https://raw.githubusercontent.com/pc-coholic/de.pccoholic.pretix.dpc/master/img/web_hi_res_512.png?token=AAn81Hvw7AdOyqR06TPo8ckk9rJy63zLks5ZeKUhwA%3D%3D" width="100" height="100" /> de.pccoholic.pretix.dpc

Android Device Policy Controller (DPC) for pretix and pretixdroid


## What is this?
A Device Policy Controller, short DPC, can be used to restrict certain functionality on Android devices and/or ensure the compliance with certain device policies, like minimum password length, etc.

This can be done with a simple Device Administrator application - many eMail apps already include such functionality in order to enforce security restrictions of Exchange-based eMail-accounts. There can be multiple Device Administrators enabled at any time.

However there is something like a Device Administrator with extended rights: The Device Owner.

This applications has total control over the device and - once installed - cannot be removed from the device anymore. The usecase for Device Owners is mostly in a corporate environment, where the devices are actually owned by the company and not BYOD-devices that are owned by employees.

As a Device Owner has a deep impact onto a device, it can only be set up on devices that have not yet an account of any kind added to them. Ideally, the provisioning is taking place during the OOBE phase.

pretixDPC is such a Device Owner/Device Administrator/Device Policy Controller.

This app is supposed to be provisioned onto __dedicated__ devices that are used to perfom CheckIn duty using [pretixdroid](https://www.github.com/pretix/pretixdroid) and that need to be bolted down for security reasons.

__Be aware:__ While pretixDPC makes it a lot harder to use the device for anything else than the scanning of entrance tickets, it is not impossible to escape the walled garden, that pretixDPC is putting up. There is no such thing as 100% security.

## Compatible devices
pretixDPC is working in theory on any Android device running Lollipop 5.1 (API-Level 22).

However, at this point it relies heavily on the integrated barcode scanner of a device known as "Lecom U8000S" and "Caribe PL-40L". This depency is to be removed in future releases.

## How to install
The app can be installed in two ways:
### Development Setup
- Reset your device and follow the OOBE - but __without__ adding any accounts (Google, eMail, etc.) to the device.
- Enable the developer mode on the device by tapping multiple times on the build-number in the Phone Settings -> About Phone
- Enable USB-debugging in the Phone Settings -> Developer options
- Install the apk of the app
- Make the app the device owner by running `adb shell dpm set-device-owner de.pccoholic.pretix.dpc/.AdminReceiver` from the console of your computer with the phone connected

### Production deployment
- Reset your device but do __not__ follow the OOBE
- Using a second, NFC-enabled Android device, install a DPC-NFC-provisioning tool like, for example, [Blue Agent Writer](https://play.google.com/work/apps/details?id=com.sdgsystems.set_device_owner)
- Create a proper profile for the provisioning, including
  - the URL of the pretixDPC apk-file
  - a friendly name of the DPC (may we recommend: "pretix DPC"?)
  - the SSID and (if applicable) credentials of a WiFi, that the phone will connect to, to download the DPC during provisioning
- Optionally: write the settings to an empty NFC tag
- Bump your phone or the newly written NFC tag to the NFC reader of the device that you want to provision.
- Follow the instructions on the phone: This may take some time, as the battery needs to be charged to 100% and the device may need to be encrypted

## Provisioning
After the installation of pretix DPC on your device, nothing will look differnt to you. This is expected.

You may now open pretix DPC from the app drawer and follow the on-screen instructions. Most likely, you will only need to provision the DPC at this point, as the app has already been set the Device Owner and Device Administrator during the OOBE provisioning.

When asked, scan a QR code containing the desired configuration for your device. We are using default JSON for this:
```json
{
  "pref_DPC_unlock_barcode": 4002590140964,
  "pref_DPC_kiosk_package": "eu.pretix.pretixdroid",
  "pref_DPC_kiosk_package_url": "http://bundesnerdrichtendienst.de/pretixdroid-1.4.apk"
}
```

For starters, you may use the above configuration - [click here](https://chart.googleapis.com/chart?chs=300x300&cht=qr&chl=%7B+%22pref_DPC_unlock_barcode%22%3A+4002590140964%2C+%22pref_DPC_kiosk_package%22%3A+%22eu.pretix.pretixdroid%22%2C+%22pref_DPC_kiosk_package_url%22%3A+%22http%3A%2F%2Fbundesnerdrichtendienst.de%2Fpretixdroid-1.4.apk%22+%7D) for a QR code containing the above settings.

Please make sure to remember your unlocking barcode, as you will need to scan this one to get back to the system UI. If you forget it, you will have to factory-reset your device.

After following the provisioning step, you will be asked to download and install the kiosk application defined in the provisioning QR code. Once you have done this, you are ready to scan your tickets.

__Note:__ If you are actually using pretixdroid as your kiosk app, you will have to properly configure pretixdroid upon the first launch, too.

## Usage
### Exiting the app
By scanning your `pref_DPC_unlock_barcode`, pretixDPC will terminate and allow you the normal usage of your device. If you used the above example configuration, you can find a corresponding QR code [here](https://chart.googleapis.com/chart?chs=300x300&cht=qr&chl=4002590140964).

### Resetting the DPC configuration
By scanning your `pref_DPC_unlock_barcode`, but prefixed with `DPCRESET_`, pretixDPC will exit and revert all settings to default. Upon the next start, you will be asked to provision the app again. If you used the above example configuration, you can find corresponding QR code [here](https://chart.googleapis.com/chart?chs=300x300&cht=qr&chl=DPCRESET_4002590140964).

__Note:__ The settings of the kiosk application itself won't be reset by this.

__Note:__ If your new configuration contains the same `pref_DPC_kiosk_package`, it won't be reinstalled - not even if the downloadable package has been updated in the meantime. This will be addressed with a future release. For the time being, you have to remove the kiosk-app by hand or reset the whole device if you also wish to reinstall the kiosk application during the provisioning of pretixDPC

### Charging
When the devices is attached to a power source - USB or cradle - it will quit the kiosk application and instead display the current charging status full screen. In a future release, this might be an switchable option.
