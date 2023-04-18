/*
 * Do not reproduce without permission in writing.
 * Copyright (c) 2023.
 */
package com.khomenko.learn.applicationweb.controller.parkinglot.assembler;

import com.khomenko.learn.applicationweb.controller.parkinglot.ParkingLotController;
import com.khomenko.learn.applicationweb.domain.parkinglot.ParkingLotEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ParkingLotEntityNumberModelAssembler implements RepresentationModelAssembler<ParkingLotEntity, EntityModel<ParkingLotEntity>> {

    @Override
    public EntityModel<ParkingLotEntity> toModel(final ParkingLotEntity lot) {
        return EntityModel.of(lot,
                linkTo(methodOn(ParkingLotController.class).getParkingLotByNumber(lot.getNumber())).withSelfRel(),
                linkTo(methodOn(ParkingLotController.class).getAllParkingLots()).withRel("api/parking-lot/all"));
    }
}
