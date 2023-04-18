package com.khomenko.learn.applicationweb.repository;

import java.util.Optional;

import com.khomenko.learn.applicationweb.domain.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByName(String username);

    Boolean existsByName(String name);

    Boolean existsByEmail(String email);
}
