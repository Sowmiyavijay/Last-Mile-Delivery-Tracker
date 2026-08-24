package com.lastmile.tracker.dto.order;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CreateRescheduleRequest {
    @NotNull
    @Future
    private LocalDate requestedDate;

    @NotBlank
    private String reason;
}
