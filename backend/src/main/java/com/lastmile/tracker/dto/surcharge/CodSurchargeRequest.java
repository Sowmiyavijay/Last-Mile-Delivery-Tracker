package com.lastmile.tracker.dto.surcharge;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CodSurchargeRequest {

    @NotNull(message = "Surcharge amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Surcharge amount must be zero or positive")
    private BigDecimal surchargeAmount;
}
