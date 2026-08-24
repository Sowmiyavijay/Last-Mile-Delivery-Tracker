package com.lastmile.tracker.dto.order;

import com.lastmile.tracker.enums.RescheduleStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class RescheduleRequestResponse {
    private Long id;
    private Long orderId;
    private String requestedByName;
    private LocalDate requestedDate;
    private String reason;
    private RescheduleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
