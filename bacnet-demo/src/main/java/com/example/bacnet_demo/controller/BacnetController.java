package com.example.bacnet_demo.controller;

import com.example.bacnet_demo.service.BacnetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class BacnetController {

    @Autowired
    private BacnetService bacnetService;

    // Check if BACnet router is connected
    @GetMapping("/checkRouter")
    public String checkRouter() {
        boolean isConnected = bacnetService.initializeBacnetRouter("0.0.0.0", 47808, "192.168.1.255");
        return isConnected ? "BACnet Router is connected." : "No BACnet Router found.";
    }

    // Get connected devices
    @GetMapping("/getDevices")
    public List<String> getDevices() {
        return bacnetService.getConnectedDevices();
    }

    // get signals from connected devices
    @GetMapping("/getDeviceSignals")
    public Map<String, Integer> getDeviceSignals() {
        return bacnetService.getDeviceSignals();
    }
}
