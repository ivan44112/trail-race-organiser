package com.intellexi.raqs.service;

import com.intellexi.raqs.persistence.domain.RaceApplicationEntity;
import com.intellexi.raqs.persistence.repository.RaceApplicationEntityRepository;
import com.intellexi.raqs.service.dto.EventMessageRequest;
import com.intellexi.raqs.service.dto.EventMessageResponse;
import com.intellexi.raqs.service.dto.RaceApplicationDto;
import com.intellexi.raqs.service.raceapplication.RaceApplicationEntityServiceImpl;
import com.intellexi.raqs.utils.OperationType;
import com.intellexi.raqs.utils.Subscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RaceApplicationEntityServiceImplTests {
    @Mock
    private RaceApplicationEntityRepository raceApplicationEntityRepository;
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;
    @InjectMocks
    private RaceApplicationEntityServiceImpl raceApplicationEntityService;
    private RaceApplicationDto raceApplicationDto;
    private RaceApplicationEntity raceApplicationEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        raceApplicationDto = RaceApplicationDto.builder()
                .id(UUID.randomUUID())
                .firstName("Subject")
                .lastName("621")
                .club("Armored Core")
                .raceId(UUID.randomUUID())
                .build();

        raceApplicationEntity = RaceApplicationEntity.builder()
                .id(raceApplicationDto.getId())
                .firstName(raceApplicationDto.getFirstName())
                .lastName(raceApplicationDto.getLastName())
                .club(raceApplicationDto.getClub())
                .raceId(raceApplicationDto.getRaceId())
                .build();
    }

    @Test
    void getAllRaceApplications_ShouldReturnListOfRaceApplications() {
        when(raceApplicationEntityRepository.findAll()).thenReturn(List.of(raceApplicationEntity));

        List<RaceApplicationEntity> raceApplications = raceApplicationEntityService.getAllRaceApplications();

        assertEquals(1, raceApplications.size());
        assertEquals("Subject", raceApplications.get(0).getFirstName());
        assertEquals("621", raceApplications.get(0).getLastName());
        assertEquals("Armored Core", raceApplications.get(0).getClub());
        verify(raceApplicationEntityRepository, times(1)).findAll();
    }

    @Test
    void getApplicationById_ShouldReturnRaceApplication() {
        when(raceApplicationEntityRepository.findById(any(UUID.class))).thenReturn(Optional.of(raceApplicationEntity));

        RaceApplicationEntity foundRace = raceApplicationEntityService.getApplicationById(raceApplicationDto.getId());

        assertNotNull(foundRace);
        assertEquals("Subject", foundRace.getFirstName());
        assertEquals("621", foundRace.getLastName());
        assertEquals("Armored Core", foundRace.getClub());
        verify(raceApplicationEntityRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void getApplicationById_ShouldThrowExceptionWhenNotFound() {
        when(raceApplicationEntityRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                raceApplicationEntityService.getApplicationById(raceApplicationDto.getId()));

        assertTrue(exception.getMessage().contains("Unable to find race application with id"));
        verify(raceApplicationEntityRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void receiveAndProcessEvent_CreateOperation_ShouldCreateRaceApplication() {
        EventMessageRequest<RaceApplicationDto> eventMessageRequest = new EventMessageRequest<>();
        eventMessageRequest.setOperationType(OperationType.CREATE);
        eventMessageRequest.setPayload(raceApplicationDto);
        eventMessageRequest.setSubscriber(Subscriber.RACE_APPLICATION);

        raceApplicationEntityService.receiveAndProcessEvent(eventMessageRequest);

        verify(raceApplicationEntityRepository, times(1)).save(any(RaceApplicationEntity.class));
        verify(simpMessagingTemplate, times(1)).convertAndSend(anyString(), any(EventMessageResponse.class));
    }

    @Test
    void receiveAndProcessEvent_UpdateOperation_ShouldUpdateRaceApplication() {
        EventMessageRequest<RaceApplicationDto> eventMessageRequest = new EventMessageRequest<>();
        eventMessageRequest.setOperationType(OperationType.UPDATE);
        eventMessageRequest.setPayload(raceApplicationDto);
        eventMessageRequest.setSubscriber(Subscriber.RACE_APPLICATION);

        when(raceApplicationEntityRepository.findById(any(UUID.class))).thenReturn(Optional.of(raceApplicationEntity));

        raceApplicationEntityService.receiveAndProcessEvent(eventMessageRequest);

        verify(raceApplicationEntityRepository, times(1)).save(any(RaceApplicationEntity.class));
        verify(simpMessagingTemplate, times(1)).convertAndSend(anyString(), any(EventMessageResponse.class));
    }

    @Test
    void receiveAndProcessEvent_DeleteOperation_ShouldDeleteRaceApplication() {
        EventMessageRequest<String> eventMessageRequest = new EventMessageRequest<>();
        eventMessageRequest.setOperationType(OperationType.DELETE);
        eventMessageRequest.setPayload(raceApplicationDto.getId().toString());
        eventMessageRequest.setSubscriber(Subscriber.RACE_APPLICATION);

        when(raceApplicationEntityRepository.existsById(any(UUID.class))).thenReturn(true);

        raceApplicationEntityService.receiveAndProcessEvent(eventMessageRequest);

        verify(raceApplicationEntityRepository, times(1)).deleteById(any(UUID.class));
        verify(simpMessagingTemplate, times(1)).convertAndSend(anyString(), any(EventMessageResponse.class));
    }

    @Test
    void receiveAndProcessEvent_UnknownOperationType_ShouldSendErrorResponse() {
        EventMessageRequest<RaceApplicationDto> eventMessageRequest = new EventMessageRequest<>();
        eventMessageRequest.setOperationType(null);
        eventMessageRequest.setPayload(raceApplicationDto);
        eventMessageRequest.setSubscriber(Subscriber.RACE_APPLICATION);

        raceApplicationEntityService.receiveAndProcessEvent(eventMessageRequest);

        verify(simpMessagingTemplate, times(1)).convertAndSend(anyString(), any(EventMessageResponse.class));
    }

    @Test
    void handleCreate_ShouldSendErrorResponseWhenExceptionOccurs() {
        EventMessageRequest<RaceApplicationDto> eventMessageRequest = new EventMessageRequest<>();
        eventMessageRequest.setOperationType(OperationType.CREATE);
        eventMessageRequest.setPayload(raceApplicationDto);
        eventMessageRequest.setSubscriber(Subscriber.RACE_APPLICATION);

        doThrow(new RuntimeException("Database error")).when(raceApplicationEntityRepository).save(any(RaceApplicationEntity.class));

        raceApplicationEntityService.receiveAndProcessEvent(eventMessageRequest);

        verify(simpMessagingTemplate, times(1)).convertAndSend(anyString(), any(EventMessageResponse.class));
    }

    @Test
    void handleUpdate_ShouldSendErrorResponseWhenRaceApplicationNotFound() {
        EventMessageRequest<RaceApplicationDto> eventMessageRequest = new EventMessageRequest<>();
        eventMessageRequest.setOperationType(OperationType.UPDATE);
        eventMessageRequest.setPayload(raceApplicationDto);
        eventMessageRequest.setSubscriber(Subscriber.RACE_APPLICATION);

        when(raceApplicationEntityRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        raceApplicationEntityService.receiveAndProcessEvent(eventMessageRequest);

        verify(simpMessagingTemplate, times(1)).convertAndSend(anyString(), any(EventMessageResponse.class));
    }

    @Test
    void handleDelete_ShouldSendErrorResponseWhenRaceApplicationNotFound() {
        EventMessageRequest<String> eventMessageRequest = new EventMessageRequest<>();
        eventMessageRequest.setOperationType(OperationType.DELETE);
        eventMessageRequest.setPayload(raceApplicationDto.getId().toString());
        eventMessageRequest.setSubscriber(Subscriber.RACE_APPLICATION);

        when(raceApplicationEntityRepository.existsById(any(UUID.class))).thenReturn(false);

        raceApplicationEntityService.receiveAndProcessEvent(eventMessageRequest);

        verify(simpMessagingTemplate, times(1)).convertAndSend(anyString(), any(EventMessageResponse.class));
    }
}