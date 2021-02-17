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
import java.util.stream.Stream;
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
    public Void savePriceReduction(PriceReductionDTO dto) {
        if(dto == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The price reduction to save is missing.");

        if(dto.getAmountDeducted() == null || dto.getAmountDeducted() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A price reduction must have the amount deducted > 0");

        if(dto.getItem() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A price reduction must me applied to an item.");

        if(dto.getEndDate() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A price reduction must have an end date.");

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
            checkDateRangeCompatibility(parentItem.getPriceReductions(), startDate, endDate);

            PriceReduction priceReduction = new PriceReduction();
            mergeDTO2Entity(dto, priceReduction, "SavePriceReductionMapping");

            parentItem.addPriceReduction(priceReduction);

            itemRepository.save(parentItem);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Invalid item, '%s' does not exists", dto.getItem().getCode()));
        }

        return null;
    }

    public PriceReductionDTO getPriceReductionFromCode(Long code) {
        Optional<PriceReduction> priceReduction = priceReductionRepository.findByCode(code);
        if(priceReduction.isPresent()) {
            return convert2DTO(priceReduction.get());
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("The price reduction '%s' does not exist", code));
    }

    public Void updatePriceReductionWithCode(PriceReductionDTO dto, Long code) {
        if(code == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A empty code has been provided.");

        if(dto == null)
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "The price reduction with the updates is missing");

        if(dto.getCode() != null) {
            if (!dto.getCode().equals(code)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        String.format("Expected to update the price reduction '%s'," +
                                " price reduction '%s' given.", code, dto.getCode()));
            }
        }

        if(dto.getAmountDeducted() != null && dto.getAmountDeducted() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A price reduction must have the amount deducted > 0");

        Optional<PriceReduction> originalPriceReduction = priceReductionRepository.findByCode(code);
        if(!originalPriceReduction.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("The price reduction '%s' does not exist", code));

        LocalDateTime endDate = dto.getEndDate() == null
                ? originalPriceReduction.get().getEndDate() : dto.getEndDate();
        LocalDateTime startDate = dto.getStartDate() == null
                ? LocalDateTime.now() : dto.getStartDate();

        if(endDate.isBefore(startDate))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "End date should be a date after start date");

        if(dto.getItem() == null || dto.getItem().getCode() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A price reduction must me applied to an item.");

        Optional<Item> item = itemRepository.findByCode(dto.getItem().getCode());
        if(item.isPresent()) {
            Item parentItem = item.get();
            checkDateRangeCompatibility(parentItem.getPriceReductions(), startDate, endDate);

            PriceReduction priceReduction = originalPriceReduction.get();
            priceReduction.setStartDate(startDate);
            priceReduction.setEndDate(endDate);
            priceReduction.setAmountDeducted(dto.getAmountDeducted());

            itemRepository.save(parentItem);

            parentItem.addPriceReduction(priceReduction);

            priceReductionRepository.save(priceReduction);
        }
        return null;
    }

    public void mergeDTO2Entity(PriceReductionDTO dto, PriceReduction entity, String mappingName) {
        if(entity != null && dto != null)
            modelMapper.map(dto, entity, mappingName);
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

    private void checkDateRangeCompatibility(List<PriceReduction> priceReductions, LocalDateTime startDate, LocalDateTime endDate) {
        if(priceReductions != null) {
            Stream<PriceReduction> stream = priceReductions.stream();
            if(stream.anyMatch(pr -> areDatesInRange(pr.getStartDate(), pr.getEndDate(), startDate, endDate))) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        String.format("A price reduction already exists in the current date range %s - %s",
                                startDate.toString(), endDate.toString()));
            }
        }
    }

    private boolean areDatesInRange(LocalDateTime startDate, LocalDateTime endDate,LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if(startDate == null || endDate == null || rangeStart == null || rangeEnd == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An internal error occurred. Try again later. If the problem persists, contact an administrator.");
        return !(startDate.compareTo(rangeStart) < 0 && endDate.compareTo(rangeStart) < 0)
                ||
                (startDate.compareTo(rangeEnd) > 0 && endDate.compareTo(rangeEnd) > 0);
    }
}
