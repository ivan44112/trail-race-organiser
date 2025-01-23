package com.intellexi.raqs.service.race;

import com.intellexi.raqs.service.dto.EventMessageRequest;
import com.intellexi.raqs.persistence.domain.RaceEntity;

import java.util.List;
import java.util.UUID;

public interface RaceEntityService {
    List<RaceEntity> getAllRaces();
    RaceEntity getRaceEntityById(UUID id);
    void receiveAndProcessEvent(EventMessageRequest<?> eventMessageRequest);
}
