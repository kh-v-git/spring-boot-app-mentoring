package com.khomenko.learn.applicationweb.service;

import java.util.List;

import com.khomenko.learn.applicationweb.domain.parkinglot.ParkingLotEntity;
import com.khomenko.learn.applicationweb.utils.exception.ApplicationBusinessException;

public interface ParkingLotService {

    List<ParkingLotEntity> getAllParkingLots();

    ParkingLotEntity getParkingLotById(final long id) throws ApplicationBusinessException;

    ParkingLotEntity getParkingLotByNumber(final int number) throws ApplicationBusinessException;

    ParkingLotEntity createParkingLot(final ParkingLotEntity parkingLot) throws ApplicationBusinessException;

    ParkingLotEntity updateParkingLot(final ParkingLotEntity parkingLot, final long id) throws ApplicationBusinessException;

    void deleteParkingLotById(final long id) throws ApplicationBusinessException;
}
