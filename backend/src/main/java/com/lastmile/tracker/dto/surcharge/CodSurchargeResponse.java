package com.lastmile.tracker.dto.surcharge;

import com.lastmile.tracker.enums.OrderType;
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
public class CodSurchargeResponse {

    private Long id;
    private OrderType orderType;
    private BigDecimal surchargeAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
