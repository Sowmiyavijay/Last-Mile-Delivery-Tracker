package com.lastmile.tracker.dto.order;

import com.lastmile.tracker.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatusUpdateRequest {
    @NotNull
    private OrderStatus status;
}
