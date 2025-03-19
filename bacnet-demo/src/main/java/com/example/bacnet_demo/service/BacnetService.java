package com.example.bacnet_demo.service;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.apdu.APDU;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BacnetService {

    private DefaultTransport transport;
    private LocalDevice localDevice;
    private boolean isInitialized = false;

    /**
     * Initializes BACnet communication.
     */
    public boolean initializeBacnetRouter(String localBindAddress, int localPort, String broadcastAddress) {
        try {
            if (isInitialized) {
                System.out.println("BACnet is already initialized.");
                return true;
            }

            // Create BACnet IP network
            System.out.println("Creating BACnet IP network...");
            IpNetwork network = new IpNetworkBuilder()
                    .withLocalBindAddress(localBindAddress)
                    .withPort(localPort)
                    .withLocalNetworkNumber(0)
                    .withBroadcast(broadcastAddress, 24)
                    .build();
            System.out.println("BACnet IP network created successfully.");

            // Create BACnet Transport Layer
            System.out.println("Initializing BACnet transport...");
            transport = new DefaultTransport(network);
            transport.initialize();
            System.out.println("BACnet transport initialized successfully.");

            // Initialize Local Device
            System.out.println("Initializing LocalDevice...");
            localDevice = new LocalDevice(1234, transport);
            localDevice.initialize();
            System.out.println("Local device initialized successfully.");

            isInitialized = true;
            System.out.println("BACnet initialized successfully.");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing BACnet: " + e.getMessage());
            return false;
        }
    }

    /**
     * Discovers connected BACnet devices.
     */
    public List<String> getConnectedDevices() {
        List<String> deviceList = new ArrayList<>();

        if (!isInitialized || localDevice == null) {
            deviceList.add("BACnet service is not initialized.");
            return deviceList;
        }

        try {
            // Send a Who-Is request globally
            System.out.println("Sending Who-Is request...");
            localDevice.sendGlobalBroadcast(new WhoIsRequest());
            System.out.println("Who-Is request sent.");

            // Wait for devices to respond
            Thread.sleep(5000);

            // Retrieve discovered BACnet devices
            List<RemoteDevice> devices = new ArrayList<>(localDevice.getRemoteDevices());

            if (devices.isEmpty()) {
                deviceList.add("No BACnet devices found.");
            } else {
                for (RemoteDevice device : devices) {
                    deviceList.add("Device ID: " + device.getInstanceNumber() + ", Address: " + device.getAddress());
                }
            }
        } catch (InterruptedException e) {
            deviceList.add("Error discovering BACnet devices: " + e.getMessage());
        }

        return deviceList;
    }

    /**
     * Shuts down the BACnet communication and cleans up resources.
     */
    public void shutdown() {
        try {
            if (localDevice != null) {
                localDevice.terminate();
                System.out.println("LocalDevice terminated.");
            }
            if (transport != null) {
                transport.terminate();
                System.out.println("Transport terminated.");
            }
            isInitialized = false;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }
}
