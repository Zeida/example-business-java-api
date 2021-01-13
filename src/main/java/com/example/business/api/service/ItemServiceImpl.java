package com.example.business.api.service;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.model.Item;
import com.example.business.api.repository.ItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
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

    public ItemDTO convert2DTO(Item entity) {
        return modelMapper.map(entity, ItemDTO.class);
    }

    public Item convert2Entity(ItemDTO dto) {
        return modelMapper.map(dto, Item.class);
    }

    public Iterable<ItemDTO> convertIterable2DTO(Iterable<Item> iterableEntities) {
        return StreamSupport.stream(iterableEntities.spliterator(), false)
                .map(item -> modelMapper.map(item, ItemDTO.class))
                .collect(Collectors.toSet());
    }

    public Iterable<Item> convertIterable2Entity(Iterable<ItemDTO> iterableDTOs) {
        return StreamSupport.stream(iterableDTOs.spliterator(), false)
                .map(itemDTO -> modelMapper.map(itemDTO, Item.class))
                .collect(Collectors.toSet());
    }
}
