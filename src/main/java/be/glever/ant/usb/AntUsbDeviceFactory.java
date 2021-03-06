package be.glever.ant.usb;

import be.glever.ant.AntException;

import javax.usb.UsbDevice;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import java.util.ArrayList;
import java.util.List;

public class AntUsbDeviceFactory {
    public static final int PRODUCT_DYNASTREAM_M_USB = 0x1009;
    public static final int PRODUCT_DYNASTREAM_USB2 = 0x1008;
    public static final int VENDOR_DYNASTREAM = 0x0fcf;

    /**
     * Keep track of AntUsbDevices, so that the same instance is reused for the same UsbDevice
     */
    private static List<AntUsbDevice> antUsbDevices = new ArrayList<>();

    /**
     * Returns a list of <b>uninitialized</b> {@link AntUsbDevice}s that are found on the system.
     *
     * @return
     * @throws AntException
     */
    public static List<AntUsbDevice> getAvailableAntDevices() throws AntException {
        try {
            List<AntUsbDevice> usbDevices = new ArrayList<>();
            usbDevices.addAll(findDevices(UsbHostManager.getUsbServices().getRootUsbHub(), VENDOR_DYNASTREAM, PRODUCT_DYNASTREAM_M_USB));
            usbDevices.addAll(findDevices(UsbHostManager.getUsbServices().getRootUsbHub(), VENDOR_DYNASTREAM, PRODUCT_DYNASTREAM_USB2));
            return usbDevices;
        } catch (SecurityException | UsbException e) {
            throw new AntException(e);
        }
    }

    private static List<AntUsbDevice> findDevices(UsbHub usbHub, int vendorId, int productId) {
        List<AntUsbDevice> resultList = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<UsbDevice> attachedUsbDevices = usbHub.getAttachedUsbDevices();
        attachedUsbDevices.forEach(device -> {
            if (device instanceof UsbHub) {
                resultList.addAll(findDevices((UsbHub) device, vendorId, productId));
            } else {
                if (device.getUsbDeviceDescriptor().idVendor() == vendorId && device.getUsbDeviceDescriptor().idProduct() == productId) {
                    resultList.add(resuseOrCreateAntUsbDevice(device));
                }
            }
        });
        return resultList;
    }

    private static AntUsbDevice resuseOrCreateAntUsbDevice(UsbDevice device) {
        AntUsbDevice foundDevice = null;

        for (AntUsbDevice antDev : antUsbDevices) {
            if (antDev.getUsbDevice() == device)
                foundDevice = antDev;
        }

        if (foundDevice == null) {
            foundDevice = new AntUsbDevice(device);
            antUsbDevices.add(foundDevice);
        }

        return foundDevice;
    }
}
