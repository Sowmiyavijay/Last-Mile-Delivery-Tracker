package com.lastmile.tracker.controller;

import com.lastmile.tracker.dto.area.AreaRequest;
import com.lastmile.tracker.dto.area.AreaResponse;
import com.lastmile.tracker.service.AreaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/areas")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @PostMapping
    public ResponseEntity<AreaResponse> createArea(@Valid @RequestBody AreaRequest request) {
        return new ResponseEntity<>(areaService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AreaResponse>> getAllAreas() {
        return ResponseEntity.ok(areaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaResponse> getArea(@PathVariable Long id) {
        return ResponseEntity.ok(areaService.findById(id));
    }

    @GetMapping("/pincode/{pincode}")
    public ResponseEntity<AreaResponse> getAreaByPincode(@PathVariable String pincode) {
        return ResponseEntity.ok(areaService.findByPincode(pincode));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AreaResponse> updateArea(@PathVariable Long id, @Valid @RequestBody AreaRequest request) {
        return ResponseEntity.ok(areaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArea(@PathVariable Long id) {
        areaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
