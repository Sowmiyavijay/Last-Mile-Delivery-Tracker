package com.lastmile.tracker.service;

import com.lastmile.tracker.dto.order.TrackingHistoryResponse;
import com.lastmile.tracker.entity.Order;
import com.lastmile.tracker.entity.TrackingHistory;
import com.lastmile.tracker.entity.User;
import com.lastmile.tracker.enums.Role;
import com.lastmile.tracker.enums.OrderStatus;
import com.lastmile.tracker.exception.ResourceNotFoundException;
import com.lastmile.tracker.repository.OrderRepository;
import com.lastmile.tracker.repository.TrackingHistoryRepository;
import com.lastmile.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackingHistoryService {

    private final TrackingHistoryRepository trackingHistoryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public void append(Order order, OrderStatus status, User actor) {
        trackingHistoryRepository.save(new TrackingHistory(order, status, LocalDateTime.now(), actor));
    }

    @Transactional(readOnly = true)
    public List<TrackingHistoryResponse> getTracking(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        User actor = currentUser();
        authorize(order, actor);
        return trackingHistoryRepository.findByOrderIdOrderByTimestampAscIdAsc(orderId).stream()
                .map(history -> TrackingHistoryResponse.builder()
                        .status(history.getStatus())
                        .timestamp(history.getTimestamp())
                        .actorName(history.getActor().getName())
                        .actorRole(history.getActor().getRole())
                        .build())
                .toList();
    }

    private void authorize(Order order, User actor) {
        if (actor.getRole() == Role.ADMIN) {
            return;
        }
        if (actor.getRole() == Role.CUSTOMER && order.getCustomer().getId().equals(actor.getId())) {
            return;
        }
        if (actor.getRole() == Role.DELIVERY_AGENT
                && order.getAssignedAgent() != null
                && order.getAssignedAgent().getUser().getId().equals(actor.getId())) {
            return;
        }
        throw new AccessDeniedException("You are not authorized to view this order tracking history");
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
