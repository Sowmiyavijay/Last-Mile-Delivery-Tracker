package com.lastmile.tracker.dto.order;

import com.lastmile.tracker.enums.OrderStatus;
import com.lastmile.tracker.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrackingHistoryResponse {
    private OrderStatus status;
    private LocalDateTime timestamp;
    private String actorName;
    private Role actorRole;
}
