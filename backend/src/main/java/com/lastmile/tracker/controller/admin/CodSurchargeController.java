package com.lastmile.tracker.controller;

import com.lastmile.tracker.dto.surcharge.CodSurchargeRequest;
import com.lastmile.tracker.dto.surcharge.CodSurchargeResponse;
import com.lastmile.tracker.enums.OrderType;
import com.lastmile.tracker.service.CodSurchargeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cod-surcharges")
@RequiredArgsConstructor
public class CodSurchargeController {

    private final CodSurchargeService codSurchargeService;

    @GetMapping
    public ResponseEntity<List<CodSurchargeResponse>> getAllSurcharges() {
        return ResponseEntity.ok(codSurchargeService.findAll());
    }

    @GetMapping("/{orderType}")
    public ResponseEntity<CodSurchargeResponse> getSurcharge(@PathVariable OrderType orderType) {
        return ResponseEntity.ok(codSurchargeService.getCodSurcharge(orderType));
    }

    @PutMapping("/{orderType}")
    public ResponseEntity<CodSurchargeResponse> updateSurcharge(@PathVariable OrderType orderType, @Valid @RequestBody CodSurchargeRequest request) {
        return ResponseEntity.ok(codSurchargeService.updateSurcharge(orderType, request));
    }
}
