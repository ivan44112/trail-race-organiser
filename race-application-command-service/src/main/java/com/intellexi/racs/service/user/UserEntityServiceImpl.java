package com.intellexi.racs.service.user;

import com.intellexi.racs.domain.UserEntity;
import com.intellexi.racs.domain.repository.UserEntityRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserEntityServiceImpl implements UserEntityService {
    private final UserEntityRepository userEntityRepository;

    public UserEntityServiceImpl(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalStateException("Provided email is invalid");
        }

        return Optional.ofNullable(userEntityRepository.findByEmail(email));
    }
}
