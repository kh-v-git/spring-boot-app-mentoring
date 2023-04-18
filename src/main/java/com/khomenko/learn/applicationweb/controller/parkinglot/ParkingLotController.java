/*
 * Do not reproduce without permission in writing.
 * Copyright (c) 2023.
 */
package com.khomenko.learn.applicationweb.controller.parkinglot;

import com.khomenko.learn.applicationweb.controller.parkinglot.assembler.ParkingLotEntityIdModelAssembler;
import com.khomenko.learn.applicationweb.controller.parkinglot.assembler.ParkingLotEntityNumberModelAssembler;
import com.khomenko.learn.applicationweb.domain.parkinglot.ParkingLotEntity;
import com.khomenko.learn.applicationweb.service.ParkingLotService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/api/" })
public class ParkingLotController {
    private final ParkingLotService parkingLotService;

    private final ParkingLotEntityNumberModelAssembler parkingLotEntityNumberModelAssembler;

    private final ParkingLotEntityIdModelAssembler parkingLotEntityIdModelAssembler;

    public ParkingLotController(final ParkingLotService parkingLotService, final ParkingLotEntityNumberModelAssembler parkingLotEntityNumberModelAssembler,
            final ParkingLotEntityIdModelAssembler parkingLotEntityIdModelAssembler) {
        this.parkingLotService = parkingLotService;
        this.parkingLotEntityNumberModelAssembler = parkingLotEntityNumberModelAssembler;
        this.parkingLotEntityIdModelAssembler = parkingLotEntityIdModelAssembler;
    }

    @GetMapping({ "parking-lot/all" })
    public CollectionModel<EntityModel<ParkingLotEntity>> getAllParkingLots() {

        return parkingLotEntityIdModelAssembler.toCollectionModel(parkingLotService.getAllParkingLots());
    }

    @GetMapping({ "parking-lot/id/{id}" })
    public EntityModel<ParkingLotEntity> getParkingLotById(@PathVariable final Long id) {
        return parkingLotEntityIdModelAssembler.toModel(parkingLotService.getParkingLotById(id));
    }

    @GetMapping({ "parking-lot/number/{number}" })
    public EntityModel<ParkingLotEntity> getParkingLotByNumber(@PathVariable final int number) {
        return parkingLotEntityNumberModelAssembler.toModel(parkingLotService.getParkingLotByNumber(number));
    }

    @PostMapping({ "parking-lot/create" })
    public ResponseEntity<?> createParkingLot(@RequestBody final ParkingLotEntity parkingLot) {
        EntityModel<ParkingLotEntity> entityModel = parkingLotEntityIdModelAssembler.toModel(parkingLotService.createParkingLot(parkingLot));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @PutMapping({ "parking-lot/update/{id}" })
    public ResponseEntity<?> updateParkingLot(@RequestBody final ParkingLotEntity parkingLot, @PathVariable final Long id) {
        EntityModel<ParkingLotEntity> entityModel = parkingLotEntityIdModelAssembler.toModel(parkingLotService.updateParkingLot(parkingLot, id));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @DeleteMapping(value = { "parking-lot/delete/{id}" })
    public ResponseEntity<?> deleteParkingLotById(@PathVariable final Long id) {
        parkingLotService.deleteParkingLotById(id);

        return ResponseEntity.noContent().build();
    }
}
