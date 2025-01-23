package com.intellexi.raqs.controller;

import com.intellexi.raqs.global.GlobalExceptionHandler;
import com.intellexi.raqs.persistence.domain.RaceEntity;
import com.intellexi.raqs.service.dto.RaceDto;
import com.intellexi.raqs.service.race.RaceEntityService;
import com.intellexi.raqs.utils.Distance;
import com.intellexi.raqs.utils.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RaceQueryControllerTests {
    @Mock
    private RaceEntityService raceEntityService;
    @InjectMocks
    private RaceQueryController raceQueryController;
    private MockMvc mockMvc;
    private RaceEntity raceEntity;
    private RaceDto raceDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(raceQueryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        raceEntity = RaceEntity.builder()
                .id(UUID.randomUUID())
                .name("CROATIAN MARATHON")
                .distance(Distance.MARATHON)
                .build();

        raceDto = Mapper.toRaceDto(raceEntity);
    }

    @Test
    void getAllRaces_ShouldReturnListOfRaces() throws Exception {
        when(raceEntityService.getAllRaces()).thenReturn(List.of(raceEntity));

        mockMvc.perform(get("/api/v1/queries/races")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(raceDto.getId().toString()))
                .andExpect(jsonPath("$[0].name").value(raceDto.getName()))
                .andExpect(jsonPath("$[0].distance").value(raceDto.getDistance().toString()));
    }

    @Test
    void getRaceById_ShouldReturnRace() throws Exception {
        when(raceEntityService.getRaceEntityById(any(UUID.class))).thenReturn(raceEntity);

        mockMvc.perform(get("/api/v1/queries/races/{id}", raceEntity.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(raceDto.getId().toString()))
                .andExpect(jsonPath("$.name").value(raceDto.getName()))
                .andExpect(jsonPath("$.distance").value(raceDto.getDistance().toString()));
    }

    @Test
    void getRaceById_ShouldReturnNotFound() throws Exception {
        when(raceEntityService.getRaceEntityById(any(UUID.class)))
                .thenThrow(new IllegalStateException("Unable to find race with id"));

        mockMvc.perform(get("/api/v1/queries/races/{id}", UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unable to find race with id"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
