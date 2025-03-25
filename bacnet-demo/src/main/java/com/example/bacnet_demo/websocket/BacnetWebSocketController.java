package com.example.bacnet_demo.websocket;

import com.example.bacnet_demo.service.BacnetService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class BacnetWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final BacnetService bacnetService;

    public BacnetWebSocketController(SimpMessagingTemplate messagingTemplate, BacnetService bacnetService) {
        this.messagingTemplate = messagingTemplate;
        this.bacnetService = bacnetService;
    }

    /**
     * Sends the connected device names to `/topic/devices` every 5 seconds.
     */
    @Scheduled(fixedRate = 5000)
    public void sendConnectedDevices() {
        List<String> devices = bacnetService.getConnectedDevices();
        messagingTemplate.convertAndSend("/topic/devices", devices);
    }

    /**
     * Sends device signals to `/topic/signals` every 5 seconds.
     */
    @Scheduled(fixedRate = 5000)
    public void sendDeviceSignals() {
        Map<String, Integer> signals = bacnetService.getDeviceSignals();
        messagingTemplate.convertAndSend("/topic/signals", signals);
    }
}
