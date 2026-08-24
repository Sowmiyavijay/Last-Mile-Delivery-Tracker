package com.lastmile.tracker.service;

import com.lastmile.tracker.dto.order.OrderResponse;
import com.lastmile.tracker.entity.DeliveryAgent;
import com.lastmile.tracker.entity.Order;
import com.lastmile.tracker.entity.User;
import com.lastmile.tracker.enums.OrderStatus;
import com.lastmile.tracker.enums.NotificationType;
import com.lastmile.tracker.enums.Role;
import com.lastmile.tracker.exception.ResourceNotFoundException;
import com.lastmile.tracker.repository.DeliveryAgentRepository;
import com.lastmile.tracker.repository.OrderRepository;
import com.lastmile.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private static final Map<OrderStatus, EnumSet<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.PENDING, EnumSet.of(OrderStatus.PICKED_UP),
            OrderStatus.PICKED_UP, EnumSet.of(OrderStatus.IN_TRANSIT),
            OrderStatus.IN_TRANSIT, EnumSet.of(OrderStatus.OUT_FOR_DELIVERY),
            OrderStatus.OUT_FOR_DELIVERY, EnumSet.of(OrderStatus.DELIVERED, OrderStatus.FAILED),
            OrderStatus.FAILED, EnumSet.of(OrderStatus.PENDING),
            OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class)
    );

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final TrackingHistoryService trackingHistoryService;
    private final OrderService orderService;
    private final RescheduleRequestService rescheduleRequestService;
    private final NotificationService notificationService;

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        User actor = currentUser();
        authorize(order, actor);
        if (!ALLOWED_TRANSITIONS.getOrDefault(order.getStatus(), EnumSet.noneOf(OrderStatus.class)).contains(newStatus)) {
            throw new IllegalArgumentException("Invalid order status transition from " + order.getStatus() + " to " + newStatus);
        }
        order.setStatus(newStatus);
        orderRepository.save(order);
        trackingHistoryService.append(order, newStatus, actor);
        NotificationType notificationType = notificationTypeFor(newStatus);
        if (notificationType != null) {
            notificationService.notifyCustomer(order.getCustomer(), order, notificationType,
                titleFor(newStatus), "Order #" + order.getId() + " is now " + newStatus + ".");
        }
        if (newStatus == OrderStatus.DELIVERED) {
            rescheduleRequestService.completeApprovedRequest(orderId);
        }
        return orderService.toResponse(order);
    }

    private NotificationType notificationTypeFor(OrderStatus status) {
        return switch (status) {
            case PICKED_UP -> NotificationType.ORDER_PICKED_UP;
            case IN_TRANSIT -> NotificationType.ORDER_IN_TRANSIT;
            case OUT_FOR_DELIVERY -> NotificationType.ORDER_OUT_FOR_DELIVERY;
            case DELIVERED -> NotificationType.ORDER_DELIVERED;
            case FAILED -> NotificationType.DELIVERY_FAILED;
            default -> null;
        };
    }

    private String titleFor(OrderStatus status) {
        return switch (status) {
            case FAILED -> "Delivery Failed";
            default -> "Order " + status;
        };
    }

    private void authorize(Order order, User actor) {
        if (actor.getRole() == Role.ADMIN) {
            return;
        }
        if (actor.getRole() != Role.DELIVERY_AGENT) {
            throw new AccessDeniedException("Customers cannot update order status");
        }
        DeliveryAgent agent = deliveryAgentRepository.findByUserId(actor.getId())
                .orElseThrow(() -> new AccessDeniedException("Delivery agent profile not found"));
        if (order.getAssignedAgent() == null || !order.getAssignedAgent().getId().equals(agent.getId())) {
            throw new AccessDeniedException("Order is not assigned to this delivery agent");
        }
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
