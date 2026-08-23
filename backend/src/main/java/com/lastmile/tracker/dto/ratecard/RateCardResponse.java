package com.lastmile.tracker.dto.ratecard;

import com.lastmile.tracker.enums.OrderType;
import com.lastmile.tracker.enums.RateType;
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
public class RateCardResponse {

    private Long id;
    private RateType rateType;
    private OrderType orderType;
    private Long pickupZoneId;
    private String pickupZoneName;
    private Long dropZoneId;
    private String dropZoneName;
    private BigDecimal baseRate;
    private BigDecimal ratePerKg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
