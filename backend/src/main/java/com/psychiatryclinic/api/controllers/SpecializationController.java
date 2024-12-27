package com.psychiatryclinic.api.controllers;

import com.psychiatryclinic.entities.enums.Specialization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/specializations")
@Tag(name = "Uzmanlık Alanları", description = "Uzmanlık alanları listesi endpoint'leri")
@CrossOrigin
public class SpecializationController {

    @GetMapping
    @Operation(
        summary = "Tüm uzmanlık alanlarını listele",
        description = "Sistemdeki tüm uzmanlık alanlarını listeler",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Uzmanlık alanları başarıyla listelendi",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class)
                )
            )
        }
    )
    public ResponseEntity<List<String>> getAll() {
        List<String> specializations = Arrays.stream(Specialization.values())
            .map(Specialization::getDisplayName)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(specializations);
    }
} 