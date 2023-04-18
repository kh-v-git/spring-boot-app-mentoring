/*
 * Do not reproduce without permission in writing.
 * Copyright (c) 2023.
 */
package com.khomenko.learn.applicationweb.service;

import java.util.List;

import com.khomenko.learn.applicationweb.domain.parkinglot.ParkingLotEntity;
import com.khomenko.learn.applicationweb.repository.ParkingLotRepository;
import com.khomenko.learn.applicationweb.utils.exception.ApplicationBusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParkingLotServiceImpl implements ParkingLotService {
    private static final Logger log = LoggerFactory.getLogger(ParkingLotServiceImpl.class);

    private final ParkingLotRepository parkingRepository;

    @Override
    public List<ParkingLotEntity> getAllParkingLots() {
        return parkingRepository.findAll();
    }

    @Override
    @Transactional
    public ParkingLotEntity createParkingLot(final ParkingLotEntity parkingLot) {
        if (parkingRepository.existsByNumber(parkingLot.getNumber())) {
            throw new ApplicationBusinessException(String.format("Parking slot with number: %s already exists", parkingLot.getNumber()));
        }
        return parkingRepository.save(parkingLot);
    }

    @Override
    public ParkingLotEntity getParkingLotById(final long id) throws ApplicationBusinessException {
        return parkingRepository.findById(id).orElseThrow(() -> new ApplicationBusinessException(String.format("No parking slot with id: %s ", id)));
    }

    @Override
    public ParkingLotEntity getParkingLotByNumber(final int number) throws ApplicationBusinessException {
        return parkingRepository.findByNumber(number).orElseThrow(() -> new ApplicationBusinessException(String.format("No parking slot with number: %s ", number)));
    }

    @Override
    @Transactional
    public ParkingLotEntity updateParkingLot(final ParkingLotEntity parkingLot, final long id) throws ApplicationBusinessException {
        return parkingRepository.findById(id).map(lot -> {
            lot.setNumber(parkingLot.getNumber());
            lot.setDescription(parkingLot.getDescription());
            lot.setStatus(parkingLot.isStatus());
            return parkingRepository.save(lot);
        }).orElseThrow(() -> new ApplicationBusinessException(String.format("Update process failed. No parking slot with id: %s ", id)));
    }

    @Override
    @Transactional
    public void deleteParkingLotById(final long id) throws ApplicationBusinessException {
        if (parkingRepository.existsById(id)) {
            parkingRepository.deleteById(id);
        } else {
            throw new ApplicationBusinessException(String.format("Delete process failed. No parking slot with id: %s ", id));
        }
    }
}
