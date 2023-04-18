/*
 * Do not reproduce without permission in writing.
 * Copyright (c) 2023.
 */
package com.khomenko.learn.applicationweb.controller.parkinglot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khomenko.learn.applicationweb.controller.parkinglot.assembler.ParkingLotEntityIdModelAssembler;
import com.khomenko.learn.applicationweb.controller.parkinglot.assembler.ParkingLotEntityNumberModelAssembler;
import com.khomenko.learn.applicationweb.domain.parkinglot.ParkingLotEntity;
import com.khomenko.learn.applicationweb.security.jwt.AuthTokenFilter;
import com.khomenko.learn.applicationweb.service.ParkingLotService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ParkingLotController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ ParkingLotEntityIdModelAssembler.class, ParkingLotEntityNumberModelAssembler.class })
class ParkingLotControllerMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParkingLotService parkingLotService;

    @MockBean
    private AuthTokenFilter authTokenFilter;

    @Test
    public void shouldGetAllParkingLots() throws Exception {
        List<ParkingLotEntity> parkingLots = new ArrayList<>();
        parkingLots.add(new ParkingLotEntity(1L, 10, "Lot 1", true));
        parkingLots.add(new ParkingLotEntity(2L, 20, "Lot 2", false));
        when(parkingLotService.getAllParkingLots()).thenReturn(parkingLots);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/parking-lot/all").accept(MediaTypes.HAL_JSON)).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void shouldGetAllParkingLotsJson() throws Exception {
        ParkingLotEntity lot1 = new ParkingLotEntity();
        lot1.setId(1L);
        lot1.setNumber(1);
        lot1.setDescription("Lot 1");
        lot1.setStatus(true);

        ParkingLotEntity lot2 = new ParkingLotEntity();
        lot2.setId(2L);
        lot2.setNumber(2);
        lot2.setDescription("Lot 2");
        lot2.setStatus(false);

        List<ParkingLotEntity> parkingLots = Arrays.asList(lot1, lot2);

        when(parkingLotService.getAllParkingLots()).thenReturn(parkingLots);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/parking-lot/all").accept(MediaTypes.HAL_JSON_VALUE))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
               .andExpect(jsonPath("$._embedded.parkingLotEntityList[0].id").value(1))
               .andExpect(jsonPath("$._embedded.parkingLotEntityList[0].number").value(1))
               .andExpect(jsonPath("$._embedded.parkingLotEntityList[0].description").value("Lot 1"))
               .andExpect(jsonPath("$._embedded.parkingLotEntityList[0].status").value(true))
               .andExpect(jsonPath("$._embedded.parkingLotEntityList[0]._links.self.href").value("http://localhost/api/parking-lot/id/1"))
               .andExpect(jsonPath("$._embedded.parkingLotEntityList[1].id").value(2))
               .andExpect(jsonPath("$._embedded.parkingLotEntityList[1].number").value(2))
               .andExpect(jsonPath("$._embedded.parkingLotEntityList[1].description").value("Lot 2"))
               .andExpect(jsonPath("$._embedded.parkingLotEntityList[1].status").value(false))
               .andExpect(jsonPath("$._embedded.parkingLotEntityList[1]._links.self.href").value("http://localhost/api/parking-lot/id/2"));
    }

    @Test
    public void shouldGetParkingLotById() throws Exception {
        ParkingLotEntity parkingLot = new ParkingLotEntity(1L, 10, "Lot 1", true);
        when(parkingLotService.getParkingLotById(1L)).thenReturn(parkingLot);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/parking-lot/id/{id}", 1L).accept(MediaTypes.HAL_JSON_VALUE))
               .andExpect(status().isOk())
               .andDo(print())
               .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.number").value(10))
               .andExpect(jsonPath("$.description").value("Lot 1"))
               .andExpect(jsonPath("$.status").value(true))
               .andExpect(jsonPath("$._links.self.href").value(String.format("http://localhost/api/parking-lot/id/%s", 1L)));
    }

    @Test
    public void shouldGetParkingLotByNumber() throws Exception {
        ParkingLotEntity parkingLot = new ParkingLotEntity(1L, 10, "Lot 1", true);
        when(parkingLotService.getParkingLotByNumber(10)).thenReturn(parkingLot);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/parking-lot/number/{number}", 10).accept(MediaTypes.HAL_JSON_VALUE))
               .andExpect(status().isOk())
               .andDo(print())
               .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.number").value(10))
               .andExpect(jsonPath("$.description").value("Lot 1"))
               .andExpect(jsonPath("$.status").value(true))
               .andExpect(jsonPath("$._links.self.href").value(String.format("http://localhost/api/parking-lot/number/%s", 10)));
    }

    @Test
    public void shouldCreateParkingLot() throws Exception {
        ParkingLotEntity parkingLot = new ParkingLotEntity(1L, 10, "Lot 1", true);
        when(parkingLotService.createParkingLot(any(ParkingLotEntity.class))).thenReturn(parkingLot);

        mockMvc.perform(
                       MockMvcRequestBuilders.post("/api/parking-lot/create").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(parkingLot)))
               .andExpect(status().isCreated())
               .andDo(print())
               .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.number").value(10))
               .andExpect(jsonPath("$.description").value("Lot 1"))
               .andExpect(jsonPath("$.status").value(true))
               .andExpect(jsonPath("$._links.self.href").value(String.format("http://localhost/api/parking-lot/id/%s", 1L)));
    }

    @Test
    public void shouldUpdateParkingLot() throws Exception {
        ParkingLotEntity parkingLot = new ParkingLotEntity(1L, 10, "Lot 1", true);
        when(parkingLotService.updateParkingLot(any(ParkingLotEntity.class), any(Long.class))).thenReturn(parkingLot);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/parking-lot/update/{id}", 1L)
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(new ObjectMapper().writeValueAsString(parkingLot)))
               .andExpect(status().isCreated())
               .andDo(print())
               .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.number").value(10))
               .andExpect(jsonPath("$.description").value("Lot 1"))
               .andExpect(jsonPath("$.status").value(true))
               .andExpect(jsonPath("$._links.self.href").value(String.format("http://localhost/api/parking-lot/id/%s", 1L)));
    }

    @Test
    public void shouldDeleteParkingLotById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/parking-lot/delete/{id}", 1L)).andExpect(status().isNoContent()).andDo(print());
        verify(parkingLotService, times(1)).deleteParkingLotById(1L);
    }
}