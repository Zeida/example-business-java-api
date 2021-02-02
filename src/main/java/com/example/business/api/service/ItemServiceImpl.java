package com.example.business.api.service;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.model.*;
import com.example.business.api.repository.ItemRepository;
import com.example.business.api.repository.PriceReductionRepository;
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

    @Autowired
    private AuthenticationFacade authenticationFacade;

    public Iterable<ItemDTO> getAllItems() {
        Iterable<Item> items = itemRepository.findAll();
        return convertIterable2DTO(items);
    }

    @Transactional
    public void saveItem(ItemDTO dto) {
        Optional<User> creator = userRepository.findByUsername(authenticationFacade.getAuthentication().getName());

        if(!creator.isPresent())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "This action cannot be done with the current user");

        if(dto.getPrice() == null || dto.getPrice().isNaN() || dto.getPrice() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An item must have a valid price");

        if(dto.getCode() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An item must have a valid code");

        if(dto.getDescription() == null || dto.getDescription().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An item must have a non-empty description");

        if(itemRepository.findByCode(dto.getCode()).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Invalid item, '%s' already exists", dto.getCode()));

        Item item = new Item();

        mergeDTO2Entity(dto, item, "SaveItemMapping");

        item.setCreator(creator.get());

        itemRepository.save(item);

        if(item.getSuppliers() != null)
            itemSuppliersProcessing(item, new HashSet<>(item.getSuppliers()));

        if(item.getPriceReductions() != null)
            itemPriceReductionsProcessing(item, new HashSet<>(item.getPriceReductions()));
    }

    public ItemDTO getItemByCode(Long code) {
        Optional<Item> item = itemRepository.findByCode(code);
        if(item.isPresent()) {
            return convert2DTO(item.get());
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("The item '%s' does not exist", code));
    }

    @Transactional
    public void updateItemWithCode(ItemDTO dto, Long code) {
        if(!dto.getCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Expected to update the item '%s', item '%s' given.", code, dto.getCode()));
        }

        Optional<Item> existingItem = itemRepository.findByCode(code);

        if(!existingItem.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("The item '%s' does not exist", code));
        }

        if(existingItem.get().getState() == ItemStateEnum.DISCONTINUED)
            throw  new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("The item '%s' has %s state, it cannot be modified.",
                            code, ItemStateEnum.DISCONTINUED.name()));

        if(dto.getPrice() != null && (dto.getPrice().isNaN() || dto.getPrice() <= 0))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An item must have a valid price");

        if(dto.getDescription() != null && dto.getDescription().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An item must have a non-empty description");

        mergeDTO2Entity(dto, existingItem.get(), "UpdateItemMapping");

        if(dto.getSuppliers() != null) {
            Set<Supplier> suppliers = StreamSupport.stream(supplierService.convertIterable2Entity(dto.getSuppliers())
                    .spliterator(), true)
                    .collect(Collectors.toSet());
            itemSuppliersProcessing(existingItem.get(), suppliers);
        }

        if(dto.getPriceReductions() != null) {
            Set<PriceReduction> priceReductions = StreamSupport.stream(priceReductionService.convertIterable2Entity(dto.getPriceReductions())
                    .spliterator(), true)
                    .collect(Collectors.toSet());
            itemPriceReductionsProcessing(existingItem.get(), priceReductions);
        }
    }

    public void deleteItem(ItemDTO dto) {
        Optional<Item> item = itemRepository.findByCode(dto.getCode());
        if(!item.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("The item '%s' does not exist", dto.getCode()));
        }
        itemRepository.delete(item.get());
    }

    public Iterable<ItemDTO> findCheapestItemPerSupplier() {
        return convertIterable2DTO(itemRepository.findCheapestItemPerSupplier());
    }

    public void deactivateItem(Long code) {
        Optional<Item> item = itemRepository.findByCode(code);
        if(!item.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("The item '%s' does not exist", code));
        }
        item.get().setState(ItemStateEnum.DISCONTINUED);
        itemRepository.save(item.get());
    }

    public void mergeDTO2Entity(ItemDTO dto, Item entity, String mappingName) {
        if(entity != null && dto != null)
            modelMapper.map(dto, entity, mappingName);
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

    private void itemSuppliersProcessing(Item item, Set<Supplier> suppliers) {
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

    private void itemPriceReductionsProcessing(Item item, Set<PriceReduction> priceReductions) {
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
}
