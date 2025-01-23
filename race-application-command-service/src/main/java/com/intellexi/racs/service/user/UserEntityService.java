package com.intellexi.racs.service.user;

import com.intellexi.racs.domain.UserEntity;

import java.util.Optional;

public interface UserEntityService {
    Optional<UserEntity> findByEmail(String email);
}
