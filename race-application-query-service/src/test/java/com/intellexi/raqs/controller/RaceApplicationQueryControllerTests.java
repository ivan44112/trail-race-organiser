package com.intellexi.raqs.controller;

import com.intellexi.raqs.global.GlobalExceptionHandler;
import com.intellexi.raqs.persistence.domain.RaceApplicationEntity;
import com.intellexi.raqs.service.dto.RaceApplicationDto;
import com.intellexi.raqs.service.raceapplication.RaceApplicationEntityService;
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

public class RaceApplicationQueryControllerTests {
    @Mock
    private RaceApplicationEntityService raceApplicationEntityService;
    @InjectMocks
    private RaceApplicationQueryController raceApplicationQueryController;
    private MockMvc mockMvc;
    private RaceApplicationEntity raceApplicationEntity;
    private RaceApplicationDto raceApplicationDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(raceApplicationQueryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        raceApplicationEntity = RaceApplicationEntity.builder()
                .id(UUID.randomUUID())
                .firstName("Subject")
                .lastName("621")
                .club("Armored Core")
                .raceId(UUID.randomUUID())
                .build();

        raceApplicationDto = Mapper.toRaceApplicationDto(raceApplicationEntity);
    }

    @Test
    void getAllRaceApplications_ShouldReturnListOfRaceApplications() throws Exception {
        when(raceApplicationEntityService.getAllRaceApplications()).thenReturn(List.of(raceApplicationEntity));

        mockMvc.perform(get("/api/v1/queries/applications")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(raceApplicationDto.getId().toString()))
                .andExpect(jsonPath("$[0].firstName").value(raceApplicationDto.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(raceApplicationDto.getLastName()))
                .andExpect(jsonPath("$[0].club").value(raceApplicationDto.getClub()))
                .andExpect(jsonPath("$[0].raceId").value(raceApplicationDto.getRaceId().toString()));
    }

    @Test
    void getRaceApplicationById_ShouldReturnRaceApplication() throws Exception {
        when(raceApplicationEntityService.getApplicationById(any(UUID.class))).thenReturn(raceApplicationEntity);

        mockMvc.perform(get("/api/v1/queries/applications/{id}", raceApplicationEntity.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(raceApplicationDto.getId().toString()))
                .andExpect(jsonPath("$.firstName").value(raceApplicationDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(raceApplicationDto.getLastName()))
                .andExpect(jsonPath("$.club").value(raceApplicationDto.getClub()))
                .andExpect(jsonPath("$.raceId").value(raceApplicationDto.getRaceId().toString()));
    }

    @Test
    void getRaceApplicationById_ShouldReturnNotFound() throws Exception {
        when(raceApplicationEntityService.getApplicationById(any(UUID.class)))
                .thenThrow(new IllegalStateException("Unable to find race application with id"));

        mockMvc.perform(get("/api/v1/queries/applications/{id}", UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unable to find race application with id"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
