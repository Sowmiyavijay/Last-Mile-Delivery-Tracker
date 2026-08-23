package com.lastmile.tracker.controller;

import com.lastmile.tracker.dto.ratecard.RateCardRequest;
import com.lastmile.tracker.dto.ratecard.RateCardResponse;
import com.lastmile.tracker.service.RateCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/rate-cards")
@RequiredArgsConstructor
public class RateCardController {

    private final RateCardService rateCardService;

    @PostMapping
    public ResponseEntity<RateCardResponse> createRateCard(@Valid @RequestBody RateCardRequest request) {
        return new ResponseEntity<>(rateCardService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RateCardResponse>> getAllRateCards() {
        return ResponseEntity.ok(rateCardService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RateCardResponse> getRateCard(@PathVariable Long id) {
        return ResponseEntity.ok(rateCardService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RateCardResponse> updateRateCard(@PathVariable Long id, @Valid @RequestBody RateCardRequest request) {
        return ResponseEntity.ok(rateCardService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRateCard(@PathVariable Long id) {
        rateCardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
