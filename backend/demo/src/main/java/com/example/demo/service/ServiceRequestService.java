package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceRequestService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private RoomRepository roomRepository;

    // Cleaning request
    public ServiceRequest requestCleaning(Long roomId) {

    Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found"));

    // Check if DND is active
    List<ServiceRequest> requests = serviceRequestRepository.findAll();

    for(ServiceRequest r : requests) {
        if(r.getRoom().getId().equals(roomId) &&
           r.getRequestType() == RequestType.DND &&
           r.getStatus().equals("ACTIVE")) {

            throw new BadRequestException("Cannot request cleaning. DND is active for this room.");
        }
    }

    ServiceRequest request = new ServiceRequest();
    request.setRoom(room);
    request.setRequestType(RequestType.CLEANING);
    request.setStatus("PENDING");
    request.setCreatedAt(LocalDateTime.now());

    return serviceRequestRepository.save(request);
}

    // Activate DND
    public ServiceRequest activateDND(Long roomId) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        ServiceRequest request = new ServiceRequest();
        request.setRoom(room);
        request.setRequestType(RequestType.DND);
        request.setStatus("ACTIVE");
        request.setCreatedAt(LocalDateTime.now());

        return serviceRequestRepository.save(request);
    }

    // View all requests
    public List<ServiceRequest> getAllRequests() {
        return serviceRequestRepository.findAll();
    }
}