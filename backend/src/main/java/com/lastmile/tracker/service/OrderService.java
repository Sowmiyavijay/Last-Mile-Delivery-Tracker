package com.lastmile.tracker.service;

import com.lastmile.tracker.dto.order.CreateOrderRequest;
import com.lastmile.tracker.dto.order.OrderResponse;
import com.lastmile.tracker.dto.order.PriceCalculationRequest;
import com.lastmile.tracker.dto.order.PriceCalculationResponse;
import com.lastmile.tracker.entity.Order;
import com.lastmile.tracker.entity.User;
import com.lastmile.tracker.exception.ResourceNotFoundException;
import com.lastmile.tracker.repository.UserRepository;
import com.lastmile.tracker.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PricingService pricingService;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        PriceCalculationRequest calcReq = new PriceCalculationRequest();
        calcReq.setPickupAddress(request.getPickupAddress());
        calcReq.setPickupPincode(request.getPickupPincode());
        calcReq.setDropAddress(request.getDropAddress());
        calcReq.setDropPincode(request.getDropPincode());
        calcReq.setOrderType(request.getOrderType());
        calcReq.setPaymentType(request.getPaymentType());
        calcReq.setActualWeight(request.getActualWeight());
        calcReq.setLength(request.getLength());
        calcReq.setWidth(request.getWidth());
        calcReq.setHeight(request.getHeight());

        PriceCalculationResponse priceRes = pricingService.calculatePrice(calcReq);

        Order order = Order.builder()
                .customer(customer)
                .pickupAddress(request.getPickupAddress())
                .pickupPincode(request.getPickupPincode())
                .dropAddress(request.getDropAddress())
                .dropPincode(request.getDropPincode())
                .orderType(request.getOrderType())
                .paymentType(request.getPaymentType())
                .actualWeight(request.getActualWeight())
                .length(request.getLength())
                .width(request.getWidth())
                .height(request.getHeight())
                .volumetricWeight(priceRes.getVolumetricWeight())
                .billingWeight(priceRes.getBillingWeight())
                .deliveryCharge(priceRes.getFinalCharge())
                .build();

        return toResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id, String customerEmail) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getCustomer().getEmail().equals(customerEmail)) {
            throw new RuntimeException("Unauthorized access to order");
        }
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

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
