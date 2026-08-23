package com.lastmile.tracker.dto.order;

import com.lastmile.tracker.enums.RateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceCalculationResponse {
    
    private String pickupZoneName;
    private String dropZoneName;
    private RateType rateType;
    
    private BigDecimal actualWeight;
    private BigDecimal volumetricWeight;
    private BigDecimal billingWeight;
    
    private BigDecimal baseRate;
    private BigDecimal ratePerKg;
    private BigDecimal deliveryCharge;
    
    private BigDecimal codSurcharge;
    private BigDecimal finalCharge;
}
