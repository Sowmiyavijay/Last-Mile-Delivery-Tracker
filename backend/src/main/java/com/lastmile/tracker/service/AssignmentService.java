package com.lastmile.tracker.service;

import com.lastmile.tracker.dto.area.AreaResponse;
import com.lastmile.tracker.dto.order.OrderResponse;
import com.lastmile.tracker.entity.DeliveryAgent;
import com.lastmile.tracker.entity.Order;
import com.lastmile.tracker.exception.ResourceNotFoundException;
import com.lastmile.tracker.repository.DeliveryAgentRepository;
import com.lastmile.tracker.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final OrderRepository orderRepository;
    private final DeliveryAgentService deliveryAgentService;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final AreaService areaService;

    @Transactional
    public void manualAssign(Long orderId, Long agentId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        if (order.getAssignedAgent() != null) {
            throw new IllegalStateException("Order is already assigned. Manual reassignment must be explicit.");
        }

        DeliveryAgent agent = deliveryAgentService.getAgentEntity(agentId);
        
        if (!agent.isAvailable()) {
            throw new IllegalStateException("Agent is not available for assignment.");
        }
        
        order.setAssignedAgent(agent);
        order.setAssignedAt(LocalDateTime.now());
        orderRepository.save(order);
    }
    
    @Transactional
    public void autoAssign(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        if (order.getAssignedAgent() != null) {
            throw new IllegalStateException("Order is already assigned.");
        }
        
        AreaResponse pickupArea = areaService.findByPincode(order.getPickupPincode());
        Long pickupZoneId = pickupArea.getZoneId();
        
        // 1. Same zone
        List<DeliveryAgent> primaryAgents = deliveryAgentRepository.findByAvailableTrueAndCurrentZoneIdOrderByIdAsc(pickupZoneId);
        if (!primaryAgents.isEmpty()) {
            order.setAssignedAgent(primaryAgents.get(0));
            order.setAssignedAt(LocalDateTime.now());
            orderRepository.save(order);
            return;
        }
        
        // 2. Global fallback
        List<DeliveryAgent> fallbackAgents = deliveryAgentRepository.findByAvailableTrueOrderByIdAsc();
        if (!fallbackAgents.isEmpty()) {
            order.setAssignedAgent(fallbackAgents.get(0));
            order.setAssignedAt(LocalDateTime.now());
            orderRepository.save(order);
            return;
        }
        
        throw new IllegalStateException("No available delivery agents to assign this order to.");
    }
}
