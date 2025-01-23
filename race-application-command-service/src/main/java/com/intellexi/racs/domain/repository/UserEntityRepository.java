package com.intellexi.racs.domain.repository;

import com.intellexi.racs.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {
    UserEntity findByEmail(String email);
}
