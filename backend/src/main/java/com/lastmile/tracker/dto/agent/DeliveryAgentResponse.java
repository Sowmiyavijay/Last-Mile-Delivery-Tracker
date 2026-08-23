package com.lastmile.tracker.dto.agent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAgentResponse {

    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private Long currentZoneId;
    private String currentZoneName;
    private boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
