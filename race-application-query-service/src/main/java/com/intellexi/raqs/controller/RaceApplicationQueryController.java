package com.intellexi.raqs.controller;

import com.intellexi.raqs.aspect.LogRequests;
import com.intellexi.raqs.persistence.domain.RaceApplicationEntity;
import com.intellexi.raqs.service.dto.RaceApplicationDto;
import com.intellexi.raqs.service.raceapplication.RaceApplicationEntityService;
import com.intellexi.raqs.utils.Mapper;
import com.intellexi.raqs.utils.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1/queries/applications")
@LogRequests
@Slf4j
public class RaceApplicationQueryController {
    private final RaceApplicationEntityService raceApplicationEntityService;

    public RaceApplicationQueryController(RaceApplicationEntityService raceApplicationEntityService) {
        this.raceApplicationEntityService = raceApplicationEntityService;
    }

    @GetMapping
    public ResponseEntity<List<RaceApplicationDto>> getAllRaceApplications() {
        List<RaceApplicationDto> raceApplications = raceApplicationEntityService
                .getAllRaceApplications()
                .stream()
                .map(Mapper::toRaceApplicationDto)
                .toList();

        return ResponseEntity.ok(raceApplications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RaceApplicationDto> getRaceApplicationById(@PathVariable UUID id) {
        RaceApplicationEntity entity;
        try {
            entity = raceApplicationEntityService.getApplicationById(id);
        } catch (IllegalStateException e) {
            throw new ValidationException(e.getMessage());
        }

        return ResponseEntity.ok(Mapper.toRaceApplicationDto(entity));
    }
}
