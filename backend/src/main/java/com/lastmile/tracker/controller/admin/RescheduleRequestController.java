package com.lastmile.tracker.controller.admin;

import com.lastmile.tracker.dto.order.RescheduleRequestResponse;
import com.lastmile.tracker.service.RescheduleRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reschedule-requests")
@RequiredArgsConstructor
public class RescheduleRequestController {

    private final RescheduleRequestService rescheduleRequestService;

    @GetMapping
    public ResponseEntity<List<RescheduleRequestResponse>> getAll() {
        return ResponseEntity.ok(rescheduleRequestService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RescheduleRequestResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(rescheduleRequestService.getById(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<RescheduleRequestResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(rescheduleRequestService.approve(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<RescheduleRequestResponse> reject(@PathVariable Long id) {
        return ResponseEntity.ok(rescheduleRequestService.reject(id));
    }
}
