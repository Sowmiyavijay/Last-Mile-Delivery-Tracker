package com.lastmile.tracker.dto.ratecard;

import com.lastmile.tracker.enums.OrderType;
import com.lastmile.tracker.enums.RateType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RateCardRequest {

    @NotNull(message = "Rate type is required")
    private RateType rateType;

    @NotNull(message = "Order type is required")
    private OrderType orderType;

    @NotNull(message = "Pickup zone ID is required")
    private Long pickupZoneId;

    @NotNull(message = "Drop zone ID is required")
    private Long dropZoneId;

    @NotNull(message = "Base rate is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Base rate must be zero or positive")
    private BigDecimal baseRate;

    @NotNull(message = "Rate per kg is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Rate per kg must be zero or positive")
    private BigDecimal ratePerKg;
}
