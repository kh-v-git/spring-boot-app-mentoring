/*
 * Do not reproduce without permission in writing.
 * Copyright (c) 2023.
 */
package com.khomenko.learn.applicationweb.repository;

import java.util.Optional;

import com.khomenko.learn.applicationweb.domain.parkinglot.ParkingLotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingLotRepository extends JpaRepository<ParkingLotEntity, Long> {
    Optional<ParkingLotEntity> findByNumber(int number);

    boolean existsByNumber(int number);
}
