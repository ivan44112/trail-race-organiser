package com.intellexi.raqs.utils;

import com.intellexi.raqs.service.dto.RaceApplicationDto;
import com.intellexi.raqs.service.dto.RaceDto;
import com.intellexi.raqs.persistence.domain.RaceApplicationEntity;
import com.intellexi.raqs.persistence.domain.RaceEntity;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    public static RaceApplicationDto toRaceApplicationDto(RaceApplicationEntity entity) {
        return RaceApplicationDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .club(entity.getClub())
                .raceId(entity.getRaceId())
                .build();

    }

    public static RaceDto toRaceDto(RaceEntity entity) {
        return RaceDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .distance(entity.getDistance())
                .build();
    }
}
