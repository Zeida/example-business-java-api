package com.example.business.api.service;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.dto.SupplierDTO;
import com.example.business.api.model.Item;
import com.example.business.api.model.PriceReduction;
import com.example.business.api.model.Supplier;
import com.example.business.api.model.User;
import com.example.business.api.repository.ItemRepository;
import com.example.business.api.repository.SupplierRepository;
import com.example.business.api.repository.UserRepository;
import com.example.business.api.security.AuthenticationFacade;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.HashSet;
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
    private UserRepository userRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    public Iterable<SupplierDTO> getAllSuppliers() {
        Iterable<Supplier> suppliers = supplierRepository.findAll();
        return convertIterable2DTO(suppliers);
    }

    @Transactional
    public void saveSupplier(SupplierDTO dto) {
        if (supplierRepository.findByName(dto.getName()).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Invalid supplier, '%s' already exists", dto.getName()));

        Supplier supplier = new Supplier();

        mergeDTO2Entity(dto, supplier, "SaveSupplierMapping");

        supplierRepository.save(supplier);

        if(supplier.getItems() != null)
            supplierItemsProcessing(supplier, new HashSet<>(dto.getItems()));
    }

    public SupplierDTO getSupplierByName(String name) {
        Optional<Supplier> supplier = supplierRepository.findByName(name);
        if (supplier.isPresent()) {
            return convert2DTO(supplier.get());
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("The supplier '%s' does not exist", name));
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
                    String.format("The supplier '%s' does not exist", name));
        }

        Supplier supplier = currentSupplier.get();

        mergeDTO2Entity(dto, supplier, "UpdateSupplierMapping");

        if(dto.getItems() != null) {
            supplierItemsProcessing(supplier, dto.getItems());
        }
    }

    public Iterable<SupplierDTO> findSuppliersWhoseItemsHasPriceReductions() {
        Iterable<Supplier> suppliers = supplierRepository.findSuppliersWhoseItemsHasPriceReductions();
        return convertIterable2DTO(suppliers);
    }

    public void mergeDTO2Entity(SupplierDTO dto, Supplier entity, String mappingName) {
        if(entity != null && dto != null)
            modelMapper.map(dto, entity, mappingName);
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

    private void supplierItemsProcessing(Supplier supplier, Set<ItemDTO> items) {
        for (ItemDTO item : items) {
            Optional<Item> itemDB = itemRepository.findByCode(item.getCode());

            if (itemDB.isPresent()) {
                itemDB.get().addSupplier(supplier);
            } else {
                Optional<User> creator = userRepository.
                        findByUsername(authenticationFacade.getAuthentication().getName());

                if(!creator.isPresent())
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "This action cannot be done with the current user");

                Item newItem = new Item();
                itemService.mergeDTO2Entity(item, newItem, "SaveItemMapping");

                itemRepository.save(newItem);
                newItem.addSupplier(supplier);
                newItem.setCreator(creator.get());
            }
        }
    }
}
