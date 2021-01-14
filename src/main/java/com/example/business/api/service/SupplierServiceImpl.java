package com.example.business.api.service;

import com.example.business.api.dto.SupplierDTO;
import com.example.business.api.model.Supplier;
import com.example.business.api.repository.SupplierRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SupplierServiceImpl implements SupplierService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SupplierRepository supplierRepository;

    public Iterable<SupplierDTO> getAllSuppliers() {
        Iterable<Supplier> suppliers = supplierRepository.findAll();
        return convertIterable2DTO(suppliers);
    }

    public void saveSupplier(SupplierDTO dto) {
        Supplier supplier = convert2Entity(dto);
        if(supplier != null)
            supplierRepository.save(supplier);
    }

    public SupplierDTO getSupplierByName(String name) {
        Optional<Supplier> supplier = supplierRepository.findByName(name);
        return supplier.map(this::convert2DTO).orElse(null);
    }

    public void updateSupplierWithName(SupplierDTO dto, String name) throws ChangeSetPersister.NotFoundException {
        Supplier supplier = convert2Entity(dto);
        Optional<Supplier> currentSupplier = supplierRepository.findByName(name);
        if(!currentSupplier.isPresent() || supplier == null || !supplier.getName().equals(name)) {
            throw new ChangeSetPersister.NotFoundException();
        }

        supplier.setId(currentSupplier.get().getId());
        supplierRepository.save(supplier);
    }

    public SupplierDTO convert2DTO(Supplier entity) {
        if(entity != null)
            return modelMapper.map(entity, SupplierDTO.class);
        return null;
    }

    public Supplier convert2Entity(SupplierDTO dto) {
        if(dto != null)
            return modelMapper.map(dto, Supplier.class);
        return null;
    }

    public Iterable<SupplierDTO> convertIterable2DTO(Iterable<Supplier> iterableEntities) {
        if(iterableEntities != null)
            return StreamSupport.stream(iterableEntities.spliterator(), false)
                    .map(supplier -> modelMapper.map(supplier, SupplierDTO.class))
                    .collect(Collectors.toList());
        return null;
    }

    public Iterable<Supplier> convertIterable2Entity(Iterable<SupplierDTO> iterableDTOs) {
        if(iterableDTOs != null)
            return StreamSupport.stream(iterableDTOs.spliterator(), false)
                    .map(supplierDTO -> modelMapper.map(supplierDTO, Supplier.class))
                    .collect(Collectors.toSet());
        return null;
    }
}
