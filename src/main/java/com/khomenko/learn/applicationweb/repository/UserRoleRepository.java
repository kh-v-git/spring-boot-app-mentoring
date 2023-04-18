package com.khomenko.learn.applicationweb.repository;

import java.util.Optional;

import com.khomenko.learn.applicationweb.domain.user.UserRoleEntity;
import com.khomenko.learn.applicationweb.domain.user.UserRolesEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    Optional<UserRoleEntity> findByRole(UserRolesEnum userRolesEnum);
}
