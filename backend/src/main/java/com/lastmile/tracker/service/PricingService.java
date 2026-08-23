package com.lastmile.tracker.service;

import com.lastmile.tracker.dto.area.AreaResponse;
import com.lastmile.tracker.dto.order.PriceCalculationRequest;
import com.lastmile.tracker.dto.order.PriceCalculationResponse;
import com.lastmile.tracker.dto.ratecard.RateCardResponse;
import com.lastmile.tracker.dto.surcharge.CodSurchargeResponse;
import com.lastmile.tracker.enums.PaymentType;
import com.lastmile.tracker.enums.RateType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final AreaService areaService;
    private final RateCardService rateCardService;
    private final CodSurchargeService codSurchargeService;

    public PriceCalculationResponse calculatePrice(PriceCalculationRequest request) {
        // 1. Resolve Areas and Zones
        AreaResponse pickupArea = areaService.findByPincode(request.getPickupPincode());
        AreaResponse dropArea = areaService.findByPincode(request.getDropPincode());

        Long pickupZoneId = pickupArea.getZoneId();
        Long dropZoneId = dropArea.getZoneId();
        
        RateType rateType = pickupZoneId.equals(dropZoneId) ? RateType.INTRA_ZONE : RateType.INTER_ZONE;

        // 2. Weights
        BigDecimal divisor = new BigDecimal("5000");
        BigDecimal volumetricWeight = request.getLength()
                .multiply(request.getWidth())
                .multiply(request.getHeight())
                .divide(divisor, 2, RoundingMode.HALF_UP);

        BigDecimal billingWeight = request.getActualWeight().max(volumetricWeight);

        // 3. Find Applicable Rate Card
        RateCardResponse rateCard = rateCardService.findApplicableRateCard(pickupZoneId, dropZoneId, request.getOrderType());

        // 4. Base Rate & Delivery Charge
        BigDecimal deliveryCharge = rateCard.getBaseRate().add(
                billingWeight.multiply(rateCard.getRatePerKg())
        ).setScale(2, RoundingMode.HALF_UP);

        // 5. COD Surcharge
        BigDecimal codSurcharge = BigDecimal.ZERO;
        if (request.getPaymentType() == PaymentType.COD) {
            CodSurchargeResponse surchargeConfig = codSurchargeService.getCodSurcharge(request.getOrderType());
            codSurcharge = surchargeConfig.getSurchargeAmount();
        }

        BigDecimal finalCharge = deliveryCharge.add(codSurcharge).setScale(2, RoundingMode.HALF_UP);

        return PriceCalculationResponse.builder()
                .pickupZoneName(pickupArea.getZoneName())
                .dropZoneName(dropArea.getZoneName())
                .rateType(rateType)
                .actualWeight(request.getActualWeight())
                .volumetricWeight(volumetricWeight)
                .billingWeight(billingWeight)
                .baseRate(rateCard.getBaseRate())
                .ratePerKg(rateCard.getRatePerKg())
                .deliveryCharge(deliveryCharge)
                .codSurcharge(codSurcharge)
                .finalCharge(finalCharge)
                .build();
    }
}
