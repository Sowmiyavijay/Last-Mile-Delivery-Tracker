package com.lastmile.tracker.controller;

import com.lastmile.tracker.dto.agent.DeliveryAgentRequest;
import com.lastmile.tracker.dto.agent.DeliveryAgentResponse;
import com.lastmile.tracker.service.DeliveryAgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/agents")
@RequiredArgsConstructor
public class DeliveryAgentController {

    private final DeliveryAgentService deliveryAgentService;

    @PostMapping
    public ResponseEntity<DeliveryAgentResponse> createAgent(@Valid @RequestBody DeliveryAgentRequest request) {
        return new ResponseEntity<>(deliveryAgentService.createAgent(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DeliveryAgentResponse>> getAllAgents() {
        return ResponseEntity.ok(deliveryAgentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryAgentResponse> getAgent(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryAgentService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryAgentResponse> updateAgent(@PathVariable Long id, @Valid @RequestBody DeliveryAgentRequest request) {
        return ResponseEntity.ok(deliveryAgentService.updateAgent(id, request));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<Void> updateAvailability(@PathVariable Long id, @RequestParam boolean available) {
        deliveryAgentService.updateAvailability(id, available);
        return ResponseEntity.noContent().build();
    }
}
