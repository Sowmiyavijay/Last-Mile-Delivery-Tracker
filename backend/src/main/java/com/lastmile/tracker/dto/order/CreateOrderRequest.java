package com.lastmile.tracker.dto.order;

// CreateOrderRequest is exactly the same as PriceCalculationRequest based on the specs
// It should contain all the same fields. 

import com.lastmile.tracker.enums.OrderType;
import com.lastmile.tracker.enums.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateOrderRequest {

    @NotBlank(message = "Pickup address is required")
    private String pickupAddress;

    @NotBlank(message = "Pickup pincode is required")
    @Pattern(regexp = "^\\d{6}$", message = "Pickup pincode must be exactly 6 digits")
    private String pickupPincode;

    @NotBlank(message = "Drop address is required")
    private String dropAddress;

    @NotBlank(message = "Drop pincode is required")
    @Pattern(regexp = "^\\d{6}$", message = "Drop pincode must be exactly 6 digits")
    private String dropPincode;

    @NotNull(message = "Order type is required")
    private OrderType orderType;

    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;

    @NotNull(message = "Actual weight is required")
    @DecimalMin(value = "0.01", message = "Actual weight must be greater than 0")
    private BigDecimal actualWeight;

    @NotNull(message = "Length is required")
    @DecimalMin(value = "0.01", message = "Length must be greater than 0")
    private BigDecimal length;

    @NotNull(message = "Width is required")
    @DecimalMin(value = "0.01", message = "Width must be greater than 0")
    private BigDecimal width;

    @NotNull(message = "Height is required")
    @DecimalMin(value = "0.01", message = "Height must be greater than 0")
    private BigDecimal height;

}
