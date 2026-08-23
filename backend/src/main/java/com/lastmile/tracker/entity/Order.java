package com.lastmile.tracker.entity;

import com.lastmile.tracker.enums.OrderStatus;
import com.lastmile.tracker.enums.OrderType;
import com.lastmile.tracker.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(nullable = false)
    private String pickupAddress;

    @Column(nullable = false, length = 6)
    private String pickupPincode;

    @Column(nullable = false)
    private String dropAddress;

    @Column(nullable = false, length = 6)
    private String dropPincode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal actualWeight;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal length;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal width;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal height;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal volumetricWeight;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal billingWeight;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryCharge;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = OrderStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
