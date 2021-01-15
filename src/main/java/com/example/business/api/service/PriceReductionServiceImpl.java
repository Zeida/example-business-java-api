package com.example.business.api.service;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.dto.PriceReductionDTO;
import com.example.business.api.model.Item;
import com.example.business.api.model.PriceReduction;
import com.example.business.api.model.Supplier;
import com.example.business.api.repository.PriceReductionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PriceReductionServiceImpl implements PriceReductionService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PriceReductionRepository priceReductionRepository;

    public Iterable<PriceReductionDTO> getAllPriceReductions() {
        Iterable<PriceReduction> priceReductions = priceReductionRepository.findAll();
        return convertIterable2DTO(priceReductions);
    }

    public void savePriceReduction(PriceReductionDTO dto) {
        PriceReduction priceReduction = convert2Entity(dto);
        if(priceReduction != null)
            priceReductionRepository.save(priceReduction);
    }

    public PriceReductionDTO getPriceReductionFromCode(Long code) {
        Optional<PriceReduction> priceReduction = priceReductionRepository.findByCode(code);
        return priceReduction.map(this::convert2DTO).orElse(null);
    }

    public void updatePriceReductionWithCode(PriceReductionDTO dto, Long code) throws ChangeSetPersister.NotFoundException {
        PriceReduction priceReduction = convert2Entity(dto);
        Optional<PriceReduction> currentPriceReduction = priceReductionRepository.findByCode(code);
        if(!currentPriceReduction.isPresent() || priceReduction == null || !priceReduction.getCode().equals(code)) {
            throw new ChangeSetPersister.NotFoundException();
        }

        priceReduction.setId(currentPriceReduction.get().getId());
        priceReductionRepository.save(priceReduction);
    }

    public PriceReductionDTO convert2DTO(PriceReduction entity) {
        if(entity != null)
            return modelMapper.map(entity, PriceReductionDTO.class);
        return null;
    }

    public PriceReduction convert2Entity(PriceReductionDTO dto) {
        if(dto != null)
            return modelMapper.map(dto, PriceReduction.class);
        return null;
    }

    public Iterable<PriceReductionDTO> convertIterable2DTO(Iterable<PriceReduction> iterableEntities) {
        if(iterableEntities != null)
            return StreamSupport.stream(iterableEntities.spliterator(), false)
                    .map(priceReduction -> modelMapper.map(priceReduction, PriceReductionDTO.class))
                    .collect(Collectors.toSet());
        return null;
    }

    public Iterable<PriceReduction> convertIterable2Entity(Iterable<PriceReductionDTO> iterableDTOs) {
        if(iterableDTOs != null)
            return StreamSupport.stream(iterableDTOs.spliterator(), false)
                    .map(priceReductionDTO -> modelMapper.map(priceReductionDTO, PriceReduction.class))
                    .collect(Collectors.toSet());
        return null;
    }
}
