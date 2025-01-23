package com.intellexi.raqs.persistence.repository;

import com.intellexi.raqs.persistence.domain.RaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface RaceEntityRepository extends JpaRepository<RaceEntity, UUID> {
}
