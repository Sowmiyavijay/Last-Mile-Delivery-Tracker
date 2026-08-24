package com.lastmile.tracker.service;

import com.lastmile.tracker.dto.order.CreateRescheduleRequest;
import com.lastmile.tracker.dto.order.RescheduleRequestResponse;
import com.lastmile.tracker.entity.Order;
import com.lastmile.tracker.entity.RescheduleRequest;
import com.lastmile.tracker.entity.User;
import com.lastmile.tracker.enums.OrderStatus;
import com.lastmile.tracker.enums.RescheduleStatus;
import com.lastmile.tracker.enums.Role;
import com.lastmile.tracker.exception.ResourceNotFoundException;
import com.lastmile.tracker.repository.OrderRepository;
import com.lastmile.tracker.repository.RescheduleRequestRepository;
import com.lastmile.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RescheduleRequestService {

    private static final EnumSet<RescheduleStatus> ACTIVE_STATUSES = EnumSet.of(
            RescheduleStatus.REQUESTED, RescheduleStatus.APPROVED);

    private final RescheduleRequestRepository rescheduleRequestRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TrackingHistoryService trackingHistoryService;

    @Transactional
    public RescheduleRequestResponse create(Long orderId, CreateRescheduleRequest request) {
        User customer = currentUser();
        requireRole(customer, Role.CUSTOMER);
        Order order = findOrder(orderId);
        authorizeCustomer(order, customer);
        if (order.getStatus() != OrderStatus.FAILED) {
            throw new IllegalArgumentException("Rescheduling is only available for failed orders");
        }
        if (!request.getRequestedDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Requested date must be in the future");
        }
        if (!rescheduleRequestRepository.findByOrderIdAndStatusIn(orderId, ACTIVE_STATUSES).isEmpty()) {
            throw new IllegalArgumentException("An active reschedule request already exists for this order");
        }

        RescheduleRequest saved = rescheduleRequestRepository.save(RescheduleRequest.builder()
                .order(order)
                .requestedBy(customer)
                .requestedDate(request.getRequestedDate())
                .reason(request.getReason())
                .status(RescheduleStatus.REQUESTED)
                .build());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<RescheduleRequestResponse> getCustomerRequests(Long orderId) {
        User customer = currentUser();
        requireRole(customer, Role.CUSTOMER);
        Order order = findOrder(orderId);
        authorizeCustomer(order, customer);
        return rescheduleRequestRepository.findByOrderIdAndRequestedByIdOrderByCreatedAtDesc(orderId, customer.getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RescheduleRequestResponse> getAll() {
        requireRole(currentUser(), Role.ADMIN);
        return rescheduleRequestRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RescheduleRequestResponse getById(Long id) {
        requireRole(currentUser(), Role.ADMIN);
        return toResponse(findRequest(id));
    }

    @Transactional
    public RescheduleRequestResponse approve(Long id) {
        User admin = currentUser();
        requireRole(admin, Role.ADMIN);
        RescheduleRequest request = findRequest(id);
        requireStatus(request, RescheduleStatus.REQUESTED);

        Order order = request.getOrder();
        if (order.getStatus() != OrderStatus.FAILED) {
            throw new IllegalArgumentException("Rescheduling is only available for failed orders");
        }
        request.setStatus(RescheduleStatus.APPROVED);
        order.setStatus(OrderStatus.PENDING);
        order.setAssignedAgent(null);
        order.setAssignedAt(null);
        rescheduleRequestRepository.save(request);
        orderRepository.save(order);
        trackingHistoryService.append(order, OrderStatus.PENDING, admin);
        return toResponse(request);
    }

    @Transactional
    public RescheduleRequestResponse reject(Long id) {
        requireRole(currentUser(), Role.ADMIN);
        RescheduleRequest request = findRequest(id);
        requireStatus(request, RescheduleStatus.REQUESTED);
        request.setStatus(RescheduleStatus.REJECTED);
        return toResponse(rescheduleRequestRepository.save(request));
    }

    @Transactional
    public void completeApprovedRequest(Long orderId) {
        List<RescheduleRequest> requests = rescheduleRequestRepository
            .findByStatusAndOrderIdOrderByCreatedAtDesc(RescheduleStatus.APPROVED, orderId);
        if (!requests.isEmpty()) {
            RescheduleRequest relevantRequest = requests.get(0);
            relevantRequest.setStatus(RescheduleStatus.COMPLETED);
            rescheduleRequestRepository.save(relevantRequest);
        }
    }

    private Order findOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    private RescheduleRequest findRequest(Long id) {
        return rescheduleRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reschedule request not found"));
    }

    private void authorizeCustomer(Order order, User customer) {
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new AccessDeniedException("You are not authorized to access this order");
        }
    }

    private void requireRole(User user, Role role) {
        if (user.getRole() != role) {
            throw new AccessDeniedException("Access denied");
        }
    }

    private void requireStatus(RescheduleRequest request, RescheduleStatus expected) {
        if (request.getStatus() != expected) {
            throw new IllegalArgumentException("Only REQUESTED reschedule requests can be changed");
        }
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    private RescheduleRequestResponse toResponse(RescheduleRequest request) {
        return RescheduleRequestResponse.builder()
                .id(request.getId())
                .orderId(request.getOrder().getId())
            .requestedByName(request.getRequestedBy().getName())
                .requestedDate(request.getRequestedDate())
                .reason(request.getReason())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
