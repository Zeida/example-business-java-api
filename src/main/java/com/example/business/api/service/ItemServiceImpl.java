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
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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

    public Iterable<ItemDTO> getAllItems() {
        Iterable<Item> items = itemRepository.findAll();
        return convertIterable2DTO(items);
    }

    public void saveItem(ItemDTO dto) throws ChangeSetPersister.NotFoundException {
        Item item = convert2Entity(dto);
        if(item != null) {
            Set<Supplier> allSuppliers = processSuppliers(item);
            if(allSuppliers == null)
                throw new ChangeSetPersister.NotFoundException();

            List<PriceReduction> allPriceReductions = processPriceReductions(item);
            if(allPriceReductions == null)
                throw new ChangeSetPersister.NotFoundException();

            item.setSuppliers(allSuppliers);
            item.setPriceReductions(allPriceReductions);

            itemRepository.save(item);
        }
    }

    public ItemDTO getItemByCode(Long code) {
        Optional<Item> item = itemRepository.findByCode(code);
        return item.map(this::convert2DTO).orElse(null);
    }

    public void updateItemWithCode(ItemDTO dto, Long code) throws ChangeSetPersister.NotFoundException {
        Item item = convert2Entity(dto);
        Optional<Item> currentItem = itemRepository.findByCode(code);
        if(!currentItem.isPresent() || item == null || !item.getCode().equals(code)) {
            throw new ChangeSetPersister.NotFoundException();
        }

        item.setId(currentItem.get().getId());
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
        Set<Supplier> existingSuppliers = item.getSuppliers().stream()
                .filter(supplier -> Objects.nonNull(supplier.getId()))
                .collect(Collectors.toSet());

        Set<Supplier> allSuppliers = item.getSuppliers().stream()
                .filter(supplier -> Objects.isNull(supplier.getId()))
                .collect(Collectors.toSet());

        for(Supplier supplier : existingSuppliers) {
            Optional<Supplier> supplierDB = supplierRepository.findByName(supplier.getName());
            if(!supplierDB.isPresent())
                return null;
            allSuppliers.add(supplierDB.get());
        }

        return allSuppliers;
    }

    private List<PriceReduction> processPriceReductions(Item item) {
        List<PriceReduction> existingPriceReductions = item.getPriceReductions().stream()
                .filter(priceReduction -> Objects.nonNull(priceReduction.getId()))
                .collect(Collectors.toList());

        List<PriceReduction> allPriceReductions = item.getPriceReductions().stream()
                .filter(priceReduction -> Objects.isNull(priceReduction.getId()))
                .collect(Collectors.toList());

        for(PriceReduction priceReduction : existingPriceReductions) {
            Optional<PriceReduction> priceReductionDB = priceReductionRepository.findById(priceReduction.getId());
            if(!priceReductionDB.isPresent())
                return null;
            allPriceReductions.add(priceReductionDB.get());
        }

        return allPriceReductions;
    }
}
