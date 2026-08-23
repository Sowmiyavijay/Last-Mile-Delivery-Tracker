package com.lastmile.tracker.controller;

import com.lastmile.tracker.dto.order.CreateOrderRequest;
import com.lastmile.tracker.dto.order.OrderResponse;
import com.lastmile.tracker.dto.order.PriceCalculationRequest;
import com.lastmile.tracker.dto.order.PriceCalculationResponse;
import com.lastmile.tracker.service.OrderService;
import com.lastmile.tracker.service.PricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PricingService pricingService;

    @PostMapping("/price")
    public ResponseEntity<PriceCalculationResponse> calculatePrice(@Valid @RequestBody PriceCalculationRequest request) {
        return ResponseEntity.ok(pricingService.calculatePrice(request));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<>(orderService.createOrder(request, email), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(orderService.getOrderById(id, email));
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(orderService.getMyOrders(email));
    }
}
