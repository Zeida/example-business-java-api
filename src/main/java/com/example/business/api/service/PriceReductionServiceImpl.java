package com.example.business.api.service;

import com.example.business.api.dto.PriceReductionDTO;
import com.example.business.api.model.Item;
import com.example.business.api.model.PriceReduction;
import com.example.business.api.repository.ItemRepository;
import com.example.business.api.repository.PriceReductionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PriceReductionServiceImpl implements PriceReductionService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PriceReductionRepository priceReductionRepository;

    @Autowired
    private ItemRepository itemRepository;

    public Iterable<PriceReductionDTO> getAllPriceReductions() {
        Iterable<PriceReduction> priceReductions = priceReductionRepository.findAll();
        return convertIterable2DTO(priceReductions);
    }

    @Transactional
    public void savePriceReduction(PriceReductionDTO dto) {
        if(dto.getAmountDeducted() == null || dto.getAmountDeducted() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A price reduction must have the amount deducted > 0");

        if(dto.getItem() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A price reduction must me applied to an item.");

        if(dto.getEndDate() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A price reduction must have an end date.");

        LocalDateTime endDate = dto.getEndDate();
        LocalDateTime startDate = dto.getStartDate() == null ? LocalDateTime.now() : dto.getStartDate();

        if(endDate.isBefore(startDate))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date should be a date after start date");

        if(priceReductionRepository.findByCode(dto.getCode()).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Invalid price reduction, '%s' already exists", dto.getCode()));

        Optional<Item> item = itemRepository.findByCode(dto.getItem().getCode());
        if(item.isPresent()) {
            Item parentItem = item.get();
            List<PriceReduction> priceReductions = parentItem.getPriceReductions();

            if(priceReductions != null) {
                if ((priceReductions.stream()
                        .anyMatch(priceReduction -> priceReduction.getStartDate().isBefore(startDate)
                                && priceReduction.getEndDate().isAfter(endDate)))) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "A price reduction already exists in the current date range.");
                }
            }
            PriceReduction priceReduction = convert2Entity(dto);
            priceReduction.setStartDate(startDate);

            parentItem.addPriceReduction(priceReduction);
            priceReduction.setItem(parentItem);

            itemRepository.save(parentItem);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Invalid item, '%s' does not exists", dto.getItem().getCode()));
        }

    }

    public PriceReductionDTO getPriceReductionFromCode(Long code) {
        Optional<PriceReduction> priceReduction = priceReductionRepository.findByCode(code);
        if(priceReduction.isPresent()) {
            return convert2DTO(priceReduction.get());
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("The price reduction '%s' does not exist", code));
    }

    public void updatePriceReductionWithCode(PriceReductionDTO dto, Long code) {
        if(!dto.getCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Expected to update the price reduction '%s'," +
                            " price reduction '%s' given.", code, dto.getCode()));
        }

        if(dto.getAmountDeducted() == null || dto.getAmountDeducted() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A price reduction must have the amount deducted > 0");

        if(dto.getItem() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A price reduction must me applied to an item.");

        if(dto.getEndDate() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A price reduction must have an end date.");

        LocalDateTime endDate = dto.getEndDate();
        LocalDateTime startDate = dto.getStartDate() == null ? LocalDateTime.now() : dto.getStartDate();

        if(endDate.isBefore(startDate))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date should be a date after start date");

        if(!priceReductionRepository.findByCode(code).isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("The price reduction '%s' does not exist", code));

        Optional<Item> item = itemRepository.findByCode(dto.getItem().getCode());
        if(item.isPresent()) {
            Item parentItem = item.get();
            if(parentItem.getPriceReductions() != null) {
                if((parentItem.getPriceReductions().stream()
                        .anyMatch(priceReduction -> priceReduction.getStartDate().isBefore(LocalDateTime.now())
                                && priceReduction.getEndDate().isAfter(LocalDateTime.now())
                                && !priceReduction.getCode().equals(code)))) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "A price reduction already exists in the current date range.");
                }
            }

            PriceReduction priceReduction = priceReductionRepository.findByCode(code).get();
            priceReduction.setStartDate(startDate);
            priceReduction.setEndDate(endDate);
            priceReduction.setAmountDeducted(dto.getAmountDeducted());

            itemRepository.save(parentItem);

            parentItem.addPriceReduction(priceReduction);

            priceReductionRepository.save(priceReduction);
        }
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
