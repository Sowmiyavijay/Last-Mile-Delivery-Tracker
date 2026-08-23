package com.lastmile.tracker.dto.order;

import com.lastmile.tracker.enums.OrderStatus;
import com.lastmile.tracker.enums.OrderType;
import com.lastmile.tracker.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    
    private Long id;
    private String pickupAddress;
    private String pickupPincode;
    private String dropAddress;
    private String dropPincode;
    
    private OrderType orderType;
    private PaymentType paymentType;
    
    private BigDecimal actualWeight;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal volumetricWeight;
    private BigDecimal billingWeight;
    
    private BigDecimal deliveryCharge;
    private OrderStatus status;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
