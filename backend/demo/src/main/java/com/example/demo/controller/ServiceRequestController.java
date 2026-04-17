package com.example.demo.controller;

import com.example.demo.entity.ServiceRequest;
import com.example.demo.service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service-requests")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestService serviceRequestService;

    // Request room cleaning
    @PostMapping("/cleaning")
    public ServiceRequest requestCleaning(@RequestParam Long roomId) {
        return serviceRequestService.requestCleaning(roomId);
    }

    // Activate Do Not Disturb
    @PostMapping("/dnd")
    public ServiceRequest activateDND(@RequestParam Long roomId) {
        return serviceRequestService.activateDND(roomId);
    }

    // View all service requests
    @GetMapping
    public List<ServiceRequest> getAllRequests() {
        return serviceRequestService.getAllRequests();
    }
}