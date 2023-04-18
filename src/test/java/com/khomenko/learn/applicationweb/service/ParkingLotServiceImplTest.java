package com.khomenko.learn.applicationweb.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.khomenko.learn.applicationweb.domain.parkinglot.ParkingLotEntity;
import com.khomenko.learn.applicationweb.repository.ParkingLotRepository;
import com.khomenko.learn.applicationweb.utils.exception.ApplicationBusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ParkingLotServiceImplTest {
    private static final String UPDATED_PARKING_LOT = "Updated parking lot";
    private static final String LOT_DESCRIPTION = "Test parking lot";
    private static final int NUMBER = 1;
    private static final long ID = 1L;

    private ParkingLotEntity parkingLot;

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @InjectMocks
    private ParkingLotServiceImpl parkingLotService;

    @BeforeEach
    public void setUp() {
        parkingLot = new ParkingLotEntity();
        parkingLot.setId(ID);
        parkingLot.setNumber(NUMBER);
        parkingLot.setDescription(LOT_DESCRIPTION);
        parkingLot.setStatus(Boolean.TRUE);
    }

    @Test
    public void testGetAllParkingLots() {
        List<ParkingLotEntity> expectedParkingLots = Collections.singletonList(parkingLot);
        Mockito.when(parkingLotRepository.findAll()).thenReturn(expectedParkingLots);

        List<ParkingLotEntity> actualParkingLots = parkingLotService.getAllParkingLots();

        assertEquals(expectedParkingLots, actualParkingLots);
    }

    @Test
    public void testCreateParkingLot() {
        Mockito.when(parkingLotRepository.existsByNumber(Mockito.anyInt())).thenReturn(Boolean.FALSE);
        Mockito.when(parkingLotRepository.save(Mockito.any(ParkingLotEntity.class))).thenReturn(parkingLot);

        ParkingLotEntity actualParkingLot = parkingLotService.createParkingLot(parkingLot);

        assertEquals(parkingLot, actualParkingLot);
    }

    @Test
    public void testCreateParkingLotWithExistingNumber() {
        Mockito.when(parkingLotRepository.existsByNumber(Mockito.anyInt())).thenReturn(Boolean.TRUE);

        assertThrows(ApplicationBusinessException.class, () -> parkingLotService.createParkingLot(parkingLot));
    }

    @Test
    public void testGetParkingLotById() {
        Mockito.when(parkingLotRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(parkingLot));

        ParkingLotEntity actualParkingLot = parkingLotService.getParkingLotById(ID);

        assertEquals(parkingLot, actualParkingLot);
    }

    @Test
    public void testGetParkingLotByInvalidId() {
        Mockito.when(parkingLotRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(ApplicationBusinessException.class, () -> parkingLotService.getParkingLotById(ID));
    }

    @Test
    public void testGetParkingLotByNumber() {
        Mockito.when(parkingLotRepository.findByNumber(Mockito.anyInt())).thenReturn(Optional.of(parkingLot));

        ParkingLotEntity actualParkingLot = parkingLotService.getParkingLotByNumber(NUMBER);

        assertEquals(parkingLot, actualParkingLot);
    }

    @Test
    public void testGetParkingLotByInvalidNumber() {
        Mockito.when(parkingLotRepository.findByNumber(Mockito.anyInt())).thenReturn(Optional.empty());

        assertThrows(ApplicationBusinessException.class, () -> parkingLotService.getParkingLotByNumber(NUMBER));
    }

    @Test
    public void testUpdateParkingLot() {
        ParkingLotEntity updatedParkingLot = new ParkingLotEntity();
        updatedParkingLot.setId(ID);
        updatedParkingLot.setNumber(2);
        updatedParkingLot.setDescription(UPDATED_PARKING_LOT);
        updatedParkingLot.setStatus(Boolean.FALSE);

        Mockito.when(parkingLotRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(parkingLot));
        Mockito.when(parkingLotRepository.save(Mockito.any(ParkingLotEntity.class))).thenReturn(updatedParkingLot);

        ParkingLotEntity actualParkingLot = parkingLotService.updateParkingLot(updatedParkingLot, ID);

        assertEquals(updatedParkingLot, actualParkingLot);
    }

    @Test
    public void testUpdateParkingLotByInvalidId() {
        ParkingLotEntity updatedParkingLot = new ParkingLotEntity();
        updatedParkingLot.setId(ID);
        updatedParkingLot.setNumber(2);
        updatedParkingLot.setDescription(UPDATED_PARKING_LOT);
        updatedParkingLot.setStatus(Boolean.FALSE);
        Mockito.when(parkingLotRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(ApplicationBusinessException.class, () -> parkingLotService.updateParkingLot(updatedParkingLot, ID));
    }

    @Test
    public void testDeleteParkingLotById() {
        Mockito.when(parkingLotRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.TRUE);

        parkingLotService.deleteParkingLotById(NUMBER);

        assertDoesNotThrow(() -> parkingLotService.deleteParkingLotById(NUMBER));
    }

    @Test
    public void testDeleteInvalidParkingLotById() {
        Mockito.when(parkingLotRepository.existsById(Mockito.anyLong())).thenReturn(Boolean.FALSE);

        assertThrows(ApplicationBusinessException.class, () -> parkingLotService.deleteParkingLotById(ID));
    }
}