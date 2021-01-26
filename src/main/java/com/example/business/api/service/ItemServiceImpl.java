package com.example.business.api.service;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.model.Item;
import com.example.business.api.model.PriceReduction;
import com.example.business.api.model.Supplier;
import com.example.business.api.model.User;
import com.example.business.api.repository.ItemRepository;
import com.example.business.api.repository.PriceReductionRepository;
import com.example.business.api.repository.SupplierRepository;
import com.example.business.api.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private PriceReductionService priceReductionService;

    public Iterable<ItemDTO> getAllItems() {
        Iterable<Item> items = itemRepository.findAll();
        return convertIterable2DTO(items);
    }

    @Transactional
    public void saveItem(ItemDTO dto) {
        if(dto.getCreator() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An item must have an user associated");

        if(itemRepository.findByCode(dto.getCode()).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Invalid item, '%s' already exists", dto.getCode()));

        if(!userRepository.findByUsername(dto.getCreator().getUsername()).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Invalid user creator, '%s' does not exists", dto.getCode()));

        User creator = userRepository.findByUsername(dto.getCreator().getUsername()).get();

        Item item = convert2Entity(dto);
        item.setCreator(creator);

        itemRepository.save(item);

        if(item.getSuppliers() != null) {
            Set<Supplier> suppliers = new HashSet<>(item.getSuppliers());

            for(Supplier supplier : suppliers) {
                Optional<Supplier> supplierDB = supplierRepository.findByName(supplier.getName());

                if(supplierDB.isPresent()) {
                    supplierDB.get().addItem(item);
                    item.getSuppliers().remove(supplier);
                    item.addSupplier(supplierDB.get());
                } else {
                    supplierRepository.save(supplier);
                    supplier.addItem(item);
                    item.addSupplier(supplier);
                }
            }
        }

        if(item.getPriceReductions() != null) {
            Set<PriceReduction> priceReductions = new HashSet<>(item.getPriceReductions());

            for(PriceReduction priceReduction : priceReductions) {
                Optional<PriceReduction> priceReductionDB = priceReductionRepository.findByCode(priceReduction.getCode());

                if(priceReductionDB.isPresent()) {
                    priceReductionDB.get().setItem(item);
                } else {
                    priceReduction.setItem(item);
                    priceReductionRepository.save(priceReduction);
                }
            }
        }
    }

    public ItemDTO getItemByCode(Long code) {
        Optional<Item> item = itemRepository.findByCode(code);
        if(item.isPresent()) {
            return convert2DTO(item.get());
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("The item '%s' doest not exist", code));
    }

    @Transactional
    public void updateItemWithCode(ItemDTO dto, Long code) {
        if(!dto.getCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Expected to update the item '%s'," +
                            " item '%s' given.", code, dto.getCode()));
        }

        if(!itemRepository.findByCode(code).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("The item '%s' doest not exists", code));
        }

        Item item = itemRepository.findByCode(code).get();
        item.setState(dto.getState());
        item.setPrice(dto.getPrice());
        item.setDescription(dto.getDescription());

        Iterable<Supplier> suppliers = supplierService.convertIterable2Entity(dto.getSuppliers());

        for(Supplier supplier : suppliers) {
            Optional<Supplier> supplierDB = supplierRepository.findByName(supplier.getName());

            if(supplierDB.isPresent()) {
                supplierDB.get().addItem(item);
                item.addSupplier(supplierDB.get());
            } else {
                supplierRepository.save(supplier);
                supplier.addItem(item);
                item.addSupplier(supplier);
            }
        }

        Iterable<PriceReduction> priceReductions = priceReductionService.convertIterable2Entity(dto.getPriceReductions());

        for(PriceReduction priceReduction : priceReductions) {
            Optional<PriceReduction> priceReductionDB = priceReductionRepository.findByCode(priceReduction.getCode());

            if(priceReductionDB.isPresent()) {
                priceReductionDB.get().setItem(item);
                item.addPriceReduction(priceReductionDB.get());
            } else {
                priceReduction.setItem(item);
                priceReductionRepository.save(priceReduction);
                item.addPriceReduction(priceReduction);
            }
        }
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
}
