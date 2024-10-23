package com.example.java_app.controller;

import com.example.java_app.dto.TariffDto;
import com.example.java_app.model.Tariff;
import com.example.java_app.service.TariffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tariffs")
public class TariffController {

    private final TariffService tariffService;

    @Autowired
    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    /*@GetMapping
    public List<Tariff> getAllTariffs() {
        return tariffService.getAllTariffs();
    }*/

    @GetMapping
    public ResponseEntity<List<TariffDto>> getAllTariffs() {
        List<TariffDto> tariffsDtoList = tariffService.getAllTariffsDto();
        return ResponseEntity.ok(tariffsDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tariff> getTariffById(@PathVariable Long id) {
        return tariffService.getTariffById(id)
                .map(tariff -> new ResponseEntity<>(tariff, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Tariff> createTariff(@RequestBody Tariff tariff) {
        Tariff savedTariff = tariffService.saveTariff(tariff);
        return new ResponseEntity<>(savedTariff, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTariff(@PathVariable Long id) {
        tariffService.deleteTariff(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
