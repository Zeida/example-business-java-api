package com.example.business.api.service;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.model.Item;
import com.example.business.api.model.PriceReduction;
import com.example.business.api.model.Supplier;
import com.example.business.api.repository.ItemRepository;
import com.example.business.api.repository.PriceReductionRepository;
import com.example.business.api.repository.SupplierRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ItemServiceImpl implements ItemService{
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private PriceReductionRepository priceReductionRepository;

    public Iterable<ItemDTO> getAllItems() {
        Iterable<Item> items = itemRepository.findAll();
        return convertIterable2DTO(items);
    }

    public void saveItem(ItemDTO dto) {
        if(dto.getCreator() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An item must have an user associated");

        if(itemRepository.findByCode(dto.getCode()).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Invalid item, '%s' already exists", dto.getCode()));

        Item item = convert2Entity(dto);

        Set<Supplier> suppliers = new HashSet<>();

        if(item.getSuppliers() != null) {
            for(Supplier supplier : item.getSuppliers()) {
                Optional<Supplier> dbSupplier = supplierRepository.findByName(supplier.getName());
                if(dbSupplier.isPresent()) {
                    suppliers.add(supplier);
                } else {
                    suppliers.add(supplierRepository.save(supplier));
                }
            }
        }

        item.setSuppliers(suppliers);

        itemRepository.save(item);
    }

    public ItemDTO getItemByCode(Long code) {
        Optional<Item> item = itemRepository.findByCode(code);
        if(item.isPresent()) {
            return convert2DTO(item.get());
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("The item '%s' doest not exist", code));
    }

    public void updateItemWithCode(ItemDTO dto, Long code) {
        if(!dto.getCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Expected to update the item '%s'," +
                            " item '%s' given.", code, dto.getCode()));
        }

        if(!itemRepository.findByCode(code).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("The item '%s' doest not exists", code));
        }

        Item item = convert2Entity(dto);
        itemRepository.save(item);
    }

    public ItemDTO convert2DTO(Item entity) {
        if(entity != null)
            return modelMapper.map(entity, ItemDTO.class);
        return null;
    }

    public Item convert2Entity(ItemDTO dto) {
        if(dto != null)
            return modelMapper.map(dto, Item.class);
        return null;
    }

    public Iterable<ItemDTO> convertIterable2DTO(Iterable<Item> iterableEntities) {
        if(iterableEntities != null)
            return StreamSupport.stream(iterableEntities.spliterator(), false)
                    .map(item -> modelMapper.map(item, ItemDTO.class))
                    .collect(Collectors.toSet());
        return null;
    }

    public Iterable<Item> convertIterable2Entity(Iterable<ItemDTO> iterableDTOs) {
        if(iterableDTOs != null)
            return StreamSupport.stream(iterableDTOs.spliterator(), false)
                    .map(itemDTO -> modelMapper.map(itemDTO, Item.class))
                    .collect(Collectors.toSet());
        return null;
    }

    private Set<Supplier> processSuppliers(Item item) {
        if(item.getSuppliers() == null)
            return new HashSet<>();

        Set<Supplier> existingSuppliers = item.getSuppliers().stream()
                .filter(supplier -> Objects.nonNull(supplier.getId()))
                .collect(Collectors.toSet());

        Set<Supplier> allSuppliers = item.getSuppliers().stream()
                .filter(supplier -> Objects.isNull(supplier.getId()))
                .collect(Collectors.toSet());

        for(Supplier supplier : allSuppliers) {
            Optional<Supplier> supplierDB = supplierRepository.findByName(supplier.getName());
            if(!supplierDB.isPresent()) {
                Supplier saved  = supplierRepository.save(supplier);
                supplier.setId(saved.getId());
            }
        }

        for(Supplier supplier : existingSuppliers) {
            if(supplierRepository.findByName(supplier.getName()).isPresent())
                allSuppliers.add(supplier);
        }

        return allSuppliers;
    }

    private List<PriceReduction> processPriceReductions(Item item) {
        if(item.getPriceReductions() == null)
            return new ArrayList<>();

        List<PriceReduction> existingPriceReductions = item.getPriceReductions().stream()
                .filter(priceReduction -> Objects.nonNull(priceReduction.getId()))
                .collect(Collectors.toList());

        List<PriceReduction> allPriceReductions = item.getPriceReductions().stream()
                .filter(priceReduction -> Objects.isNull(priceReduction.getId()))
                .collect(Collectors.toList());

        for(PriceReduction priceReduction : allPriceReductions) {
            Optional<PriceReduction> priceReductionDB = priceReductionRepository.findByCode(priceReduction.getCode());
            if(!priceReductionDB.isPresent()) {
                PriceReduction saved  = priceReductionRepository.save(priceReduction);
                priceReduction.setId(saved.getId());
            }
        }

        for(PriceReduction priceReduction : existingPriceReductions) {
            if(priceReductionRepository.findByCode(priceReduction.getCode()).isPresent())
                allPriceReductions.add(priceReduction);
        }

        return allPriceReductions;
    }
}
