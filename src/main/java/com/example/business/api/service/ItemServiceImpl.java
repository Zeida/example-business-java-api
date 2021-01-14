package com.example.business.api.service;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.model.Item;
import com.example.business.api.repository.ItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ItemServiceImpl implements ItemService{
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ItemRepository itemRepository;

    public Iterable<ItemDTO> getAllItems() {
        Iterable<Item> items = itemRepository.findAll();
        return convertIterable2DTO(items);
    }

    public void saveItem(ItemDTO dto) {
        Item item = convert2Entity(dto);
        if(item != null)
            itemRepository.save(item);
    }

    public ItemDTO getItemByCode(Long code) {
        Item item = itemRepository.findByCode(code);
        if(item != null)
            return convert2DTO(item);
        return null;
    }

    public void updateItemWithCode(ItemDTO dto, Long code) throws ChangeSetPersister.NotFoundException {
        Item item = convert2Entity(dto);
        Item currentItem = itemRepository.findByCode(code);
        if(currentItem == null || item == null || !item.getCode().equals(code)) {
            throw new ChangeSetPersister.NotFoundException();
        }

        item.setId(currentItem.getId());
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
}
