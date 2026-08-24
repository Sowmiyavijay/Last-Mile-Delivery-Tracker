package com.lastmile.tracker.service;

import com.lastmile.tracker.dto.NotificationResponse;
import com.lastmile.tracker.entity.DeliveryAgent;
import com.lastmile.tracker.entity.Notification;
import com.lastmile.tracker.entity.Order;
import com.lastmile.tracker.entity.User;
import com.lastmile.tracker.enums.NotificationType;
import com.lastmile.tracker.enums.Role;
import com.lastmile.tracker.exception.ResourceNotFoundException;
import com.lastmile.tracker.repository.NotificationRepository;
import com.lastmile.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailNotificationService emailNotificationService;

    @Transactional
    public Notification createNotification(User recipient, Order order, NotificationType type,
                                           String title, String message) {
        Notification notification = notificationRepository.save(Notification.builder()
                .recipient(recipient)
                .order(order)
                .type(type)
                .title(title)
                .message(message)
                .build());
        if (shouldEmail(type)) {
            emailNotificationService.send(recipient, title, message);
        }
        return notification;
    }

    public void notifyCustomer(User customer, Order order, NotificationType type, String title, String message) {
        createNotification(customer, order, type, title, message);
    }

    public void notifyAgent(DeliveryAgent agent, Order order, NotificationType type, String title, String message) {
        createNotification(agent.getUser(), order, type, title, message);
    }

    public void notifyAdmins(Order order, NotificationType type, String title, String message) {
        userRepository.findByRole(Role.ADMIN)
                .forEach(admin -> createNotification(admin, order, type, title, message));
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMine() {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(currentUser().getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public long unreadCount() {
        return notificationRepository.countByRecipientIdAndIsReadFalse(currentUser().getId());
    }

    @Transactional
    public NotificationResponse markRead(Long id) {
        Notification notification = notificationRepository.findByIdAndRecipientId(id, currentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setRead(true);
        return toResponse(notificationRepository.save(notification));
    }

    @Transactional
    public void markAllRead() {
        User user = currentUser();
        List<Notification> notifications = notificationRepository
                .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    private boolean shouldEmail(NotificationType type) {
        return type == NotificationType.ORDER_CREATED
                || type == NotificationType.ORDER_ASSIGNED
                || type == NotificationType.ORDER_DELIVERED
                || type == NotificationType.DELIVERY_FAILED
                || type == NotificationType.RESCHEDULE_APPROVED
                || type == NotificationType.RESCHEDULE_REJECTED;
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .orderId(notification.getOrder() == null ? null : notification.getOrder().getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}