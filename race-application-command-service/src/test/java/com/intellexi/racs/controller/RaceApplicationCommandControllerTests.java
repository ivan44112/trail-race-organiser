package com.intellexi.racs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellexi.racs.dto.EventMessageRequest;
import com.intellexi.racs.dto.RaceApplicationDto;
import com.intellexi.racs.global.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RaceApplicationCommandControllerTests {

    private MockMvc mockMvc;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private RaceApplicationCommandController raceApplicationCommandController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(raceApplicationCommandController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createRaceApplication_ValidInput_ReturnsSuccess() throws Exception {
        RaceApplicationDto application = new RaceApplicationDto();
        application.setFirstName("John");
        application.setLastName("Doe");
        application.setClub("Running Club");
        application.setRaceId(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/commands/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(application)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageContent").value("CREATE event sent to topic successfully."));

        verify(simpMessagingTemplate, times(1)).convertAndSend(
                eq("/topic/commands"),
                any(EventMessageRequest.class)
        );
    }

    @Test
    void createRaceApplication_InvalidInput_ThrowsValidationException() throws Exception {
        RaceApplicationDto application = new RaceApplicationDto();

        mockMvc.perform(post("/api/v1/commands/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(application)))
                .andExpect(status().isBadRequest())
                .andReturn();

        mockMvc.perform(post("/api/v1/commands/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(application)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("FIRST_NAME_NOT_VALID"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void updateRaceApplication_ValidInput_ReturnsSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        RaceApplicationDto application = new RaceApplicationDto();
        application.setId(id);
        application.setFirstName("Jane");
        application.setLastName("Doe");
        application.setClub("Marathon Club");

        mockMvc.perform(patch("/api/v1/commands/applications/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(application)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageContent").value("UPDATE event sent to topic successfully."));

        verify(simpMessagingTemplate, times(1)).convertAndSend(
                eq("/topic/commands"),
                any(EventMessageRequest.class)
        );
    }

    @Test
    void deleteRaceApplication_ValidId_ReturnsSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/commands/applications/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageContent").value("DELETE event sent to topic successfully."));

        verify(simpMessagingTemplate, times(1)).convertAndSend(
                eq("/topic/commands"),
                any(EventMessageRequest.class)
        );
    }
}