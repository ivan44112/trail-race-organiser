package com.intellexi.raqs.controller;


import com.intellexi.raqs.aspect.LogRequests;
import com.intellexi.raqs.persistence.domain.RaceEntity;
import com.intellexi.raqs.service.dto.RaceDto;
import com.intellexi.raqs.service.race.RaceEntityService;
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
@RequestMapping("/api/v1/queries/races")
@LogRequests
@Slf4j
public class RaceQueryController {
    private final RaceEntityService raceEntityService;

    public RaceQueryController(RaceEntityService raceEntityService) {
        this.raceEntityService = raceEntityService;
    }

    @GetMapping
    public ResponseEntity<List<RaceDto>> getAllRaces() {
        List<RaceDto> races = raceEntityService
                .getAllRaces()
                .stream()
                .map(Mapper::toRaceDto)
                .toList();

        return ResponseEntity.ok(races);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RaceDto> getRaceById(@PathVariable UUID id) {
        RaceEntity entity;
        try {
            entity = raceEntityService.getRaceEntityById(id);
        } catch (IllegalStateException e) {
            throw new ValidationException(e.getMessage());
        }

        return ResponseEntity.ok(Mapper.toRaceDto(entity));
    }
}
