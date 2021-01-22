package com.example.business.api.service;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.dto.SupplierDTO;
import com.example.business.api.model.Item;
import com.example.business.api.model.Supplier;
import com.example.business.api.repository.ItemRepository;
import com.example.business.api.repository.SupplierRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SupplierServiceImpl implements SupplierService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    public Iterable<SupplierDTO> getAllSuppliers() {
        Iterable<Supplier> suppliers = supplierRepository.findAll();
        return convertIterable2DTO(suppliers);
    }

    @Transactional
    public void saveSupplier(SupplierDTO dto) {
        if (supplierRepository.findByName(dto.getName()).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Invalid supplier, '%s' already exists", dto.getName()));

        Supplier supplier = convert2Entity(dto);

        supplierRepository.save(supplier);

        Set<Item> items = new HashSet<>(supplier.getItems());

        for(Item item : items) {
            Optional<Item> itemDB = itemRepository.findByCode(item.getCode());

            if(itemDB.isPresent()) {
                itemDB.get().addSupplier(supplier);
            } else {
                itemRepository.save(item);
                item.addSupplier(supplier);
            }
        }
    }

    public SupplierDTO getSupplierByName(String name) {
        Optional<Supplier> supplier = supplierRepository.findByName(name);
        if (supplier.isPresent()) {
            return convert2DTO(supplier.get());
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("The supplier '%s' doest not exist", name));
    }

    @Transactional
    public void updateSupplierWithName(SupplierDTO dto, String name) {
        if (!name.equals(dto.getName()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Expected to update the supplier '%s'," +
                            " supplier '%s' given.", name, dto.getName()));

        Optional<Supplier> currentSupplier = supplierRepository.findByName(name);
        if (!currentSupplier.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("The supplier '%s' doest not exist", name));
        }

        Supplier supplier = currentSupplier.get();

        supplier.setCountry(dto.getCountry());

        Iterable<Item> items = itemService.convertIterable2Entity(dto.getItems());

        for(Item item : items) {
            Optional<Item> itemDB = itemRepository.findByCode(item.getCode());

            if(itemDB.isPresent()) {
                itemDB.get().addSupplier(supplier);
                supplier.addItem(itemDB.get());
            } else {
                itemRepository.save(item);
                item.addSupplier(supplier);
                supplier.addItem(item);
            }
        }
    }

    public SupplierDTO convert2DTO(Supplier entity) {
        if (entity != null)
            return modelMapper.map(entity, SupplierDTO.class);
        return null;
    }

    public Supplier convert2Entity(SupplierDTO dto) {
        if (dto != null)
            return modelMapper.map(dto, Supplier.class);
        return null;
    }

    public Iterable<SupplierDTO> convertIterable2DTO(Iterable<Supplier> iterableEntities) {
        if (iterableEntities != null)
            return StreamSupport.stream(iterableEntities.spliterator(), false)
                    .map(supplier -> modelMapper.map(supplier, SupplierDTO.class))
                    .collect(Collectors.toList());
        return null;
    }

    public Iterable<Supplier> convertIterable2Entity(Iterable<SupplierDTO> iterableDTOs) {
        if (iterableDTOs != null)
            return StreamSupport.stream(iterableDTOs.spliterator(), false)
                    .map(supplierDTO -> modelMapper.map(supplierDTO, Supplier.class))
                    .collect(Collectors.toSet());
        return null;
    }
}
