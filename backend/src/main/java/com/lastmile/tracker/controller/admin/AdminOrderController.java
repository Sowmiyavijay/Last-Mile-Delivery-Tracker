package com.lastmile.tracker.controller;

import com.lastmile.tracker.dto.order.OrderResponse;
import com.lastmile.tracker.entity.Order;
import com.lastmile.tracker.exception.ResourceNotFoundException;
import com.lastmile.tracker.repository.OrderRepository;
import com.lastmile.tracker.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderRepository orderRepository;
    private final AssignmentService assignmentService;

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        return ResponseEntity.ok(toResponse(order));
    }
    
    @PutMapping("/{orderId}/assign/{agentId}")
    public ResponseEntity<Void> manualAssign(@PathVariable Long orderId, @PathVariable Long agentId) {
        assignmentService.manualAssign(orderId, agentId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{orderId}/auto-assign")
    public ResponseEntity<Void> autoAssign(@PathVariable Long orderId) {
        assignmentService.autoAssign(orderId);
        return ResponseEntity.ok().build();
    }
    
    // Quick manual mapping for the minimal admin lookup since AdminOrderController is only bridging the gap
    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .pickupAddress(order.getPickupAddress())
                .pickupPincode(order.getPickupPincode())
                .dropAddress(order.getDropAddress())
                .dropPincode(order.getDropPincode())
                .orderType(order.getOrderType())
                .paymentType(order.getPaymentType())
                .actualWeight(order.getActualWeight())
                .length(order.getLength())
                .width(order.getWidth())
                .height(order.getHeight())
                .volumetricWeight(order.getVolumetricWeight())
                .billingWeight(order.getBillingWeight())
                .deliveryCharge(order.getDeliveryCharge())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
