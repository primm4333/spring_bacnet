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

    /**
     * Initializes BACnet communication.
     */
    public boolean isBacnetRouterConnected(String localBindAddress, int localPort, String broadcastAddress) {
        try {
            // Create BACnet IP network
            IpNetwork network = new IpNetworkBuilder()
                    .withLocalBindAddress(localBindAddress)
                    .withPort(localPort)
                    .withLocalNetworkNumber(0)
                    .withBroadcast(broadcastAddress, 24)
                    .build();

            // Create BACnet Transport Layer
            transport = new DefaultTransport(network);
            transport.initialize();

            // Initialize Local Device
            localDevice = new LocalDevice(1234, transport);
            localDevice.initialize();

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
    public String getConnectedDevices() {
        if (localDevice == null) {
            return "BACnet service is not initialized.";
        }

        try {
            // Send a Who-Is request globally
            localDevice.sendGlobalBroadcast(new WhoIsRequest());  //

            // Wait for devices to respond
            Thread.sleep(5000);

            // Retrieve discovered BACnet devices
            List<RemoteDevice> devices = new ArrayList<>(localDevice.getRemoteDevices());

            if (devices.isEmpty()) {
                return "No BACnet devices found.";
            }

            // Format the response
            StringBuilder response = new StringBuilder("Discovered BACnet Devices:\n");
            for (RemoteDevice device : devices) {
                response.append(" Device ID: ").append(device.getInstanceNumber())
                        .append(", Address: ").append(device.getAddress())
                        .append("\n");
            }
            return response.toString();

        } catch (InterruptedException e) {
            e.printStackTrace();
            return "Error discovering BACnet devices: " + e.getMessage();
        }
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
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }
}
