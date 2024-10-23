package com.example.java_app.service;

import com.example.java_app.dto.TariffDto;
import com.example.java_app.model.Tariff;
import com.example.java_app.repository.TariffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TariffService {

    private final TariffRepository tariffRepository;

    @Autowired
    public TariffService(TariffRepository tariffRepository) {
        this.tariffRepository = tariffRepository;
    }

    public List<Tariff> getAllTariffs() {
        return tariffRepository.findAll();
    }

    private TariffDto convertToDto(Tariff tariff) {
        return new TariffDto(tariff.getId(), tariff.getDurationMonths(), tariff.getPrice());
    }

    public List<TariffDto> getAllTariffsDto() {
        List<Tariff> tariffsList = tariffRepository.findAll();
        return tariffsList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<Tariff> getTariffById(Long id) {
        return tariffRepository.findById(id);
    }

    public Tariff saveTariff(Tariff tariff) {
        return tariffRepository.save(tariff);
    }

    public void deleteTariff(Long id) {
        tariffRepository.deleteById(id);
    }
}
