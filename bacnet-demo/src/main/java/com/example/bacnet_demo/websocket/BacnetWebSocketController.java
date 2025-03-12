package com.example.bacnet_demo.websocket;

import com.example.bacnet_demo.service.BacnetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class BacnetWebSocketController {

    @Autowired
    private BacnetService bacnetService;

    @MessageMapping("/checkRouter")
    @SendTo("/topic/routerStatus")
    public String checkRouter() {
        boolean isConnected = bacnetService.isBacnetRouterConnected("0.0.0.0", 47808, "192.168.1.255");
        return isConnected ? "BACnet Router is connected." : "No BACnet Router found.";
    }

    @MessageMapping("/getDevices")
    @SendTo("/topic/devices")
    public String getDevices() {
        return bacnetService.getConnectedDevices();
    }
}
