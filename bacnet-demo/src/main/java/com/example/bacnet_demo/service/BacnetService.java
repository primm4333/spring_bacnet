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

import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class BacnetService {

    private AtomicBoolean isInitialized = new AtomicBoolean(false);
    private final List<String> simulatedDevices = new ArrayList<>();
    private final Map<String, Integer> deviceSignals = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private ScheduledExecutorService scheduler;

    /**
     * Simulates the initialization of a BACnet router.
     */
    public boolean initializeBacnetRouter(String localBindAddress, int localPort, String broadcastAddress) {
        if (isInitialized.get()) {
            return true;
        }

        System.out.println("Simulated BACnet Router initialized at " + localBindAddress + ":" + localPort);

        // Simulate adding IoT devices at home
        simulatedDevices.add("AC Unit - Living Room");
        simulatedDevices.add("Smart Light - Bedroom");
        simulatedDevices.add("Thermostat - Kitchen");

        // Initialize signals for each device
        for (String device : simulatedDevices) {
            deviceSignals.put(device, random.nextInt(100)); // Random signal strength
        }

        // Start continuous signal simulation
        startContinuousSignalSimulation();

        isInitialized.set(true);
        return true;
    }

    /**
     * Starts continuous signal simulation for each device in separate threads
     */
    private void startContinuousSignalSimulation() {
        scheduler = Executors.newScheduledThreadPool(simulatedDevices.size());

        // Create a separate task for each device
        for (String device : simulatedDevices) {
            scheduler.scheduleAtFixedRate(() -> {
                // Simulate signal updates for this specific device
                int currentSignal = deviceSignals.get(device);
                int newSignal = currentSignal + random.nextInt(5) - 2; // Slight variation
                newSignal = Math.max(0, Math.min(100, newSignal)); // Keep within range 0-100

                deviceSignals.put(device, newSignal);
                System.out.println("Device: " + device + ", Signal: " + newSignal);
            }, 0, 2, TimeUnit.SECONDS); // Update every 2 seconds
        }

        System.out.println("Continuous signal simulation started for " + simulatedDevices.size() + " devices");
    }

    /**
     * Simulates fetching connected BACnet devices.
     */
    public List<String> getConnectedDevices() {
        if (!isInitialized.get()) {
            return List.of("BACnet service is not initialized.");
        }
        return new ArrayList<>(simulatedDevices);
    }

    /**
     * Returns the current device signals.
     * No need to simulate updates here as they're handled by the continuous simulation threads.
     */
    public Map<String, Integer> getDeviceSignals() {
        if (!isInitialized.get()) {
            return Map.of("Error", -1);
        }
        return new HashMap<>(deviceSignals);
    }

    /**
     * Stop the simulation threads when the service is destroyed
     */
    @PreDestroy
    public void shutdown() {
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("BACnet simulation stopped");
    }
}