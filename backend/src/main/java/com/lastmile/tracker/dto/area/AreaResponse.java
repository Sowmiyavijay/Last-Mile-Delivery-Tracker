package com.lastmile.tracker.dto.area;

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
public class AreaResponse {

    private Long id;
    private String name;
    private String pincode;
    private Long zoneId;
    private String zoneName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
