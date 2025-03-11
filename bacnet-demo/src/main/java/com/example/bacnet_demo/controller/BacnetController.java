package com.example.bacnet_demo.controller;

import com.example.bacnet_demo.service.BacnetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BacnetController {

    @Autowired
    private BacnetService bacnetService;

    // Check if BACnet router is connected
    @GetMapping("/checkRouter")
    public String checkRouter(
            @RequestParam String localBindAddress,  // Local bind address (e.g., "0.0.0.0")
            @RequestParam int localPort,            // Local BACnet port (e.g., 47808)
            @RequestParam String broadcastAddress   // BACnet broadcast address (e.g., "192.168.1.255")
    ) {
        boolean isConnected = bacnetService.isBacnetRouterConnected(localBindAddress, localPort, broadcastAddress);
        return isConnected ? "BACnet Router is connected." : "No BACnet Router found.";
    }

    // Get connected devices
    @GetMapping("/getDevices")
    public String getDevices() {
        return bacnetService.getConnectedDevices();
    }
}
