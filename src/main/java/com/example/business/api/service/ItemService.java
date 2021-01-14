package com.example.business.api.service;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.model.Item;
import org.springframework.data.crossstore.ChangeSetPersister;

public interface ItemService extends BaseService<Item, ItemDTO>{
    Iterable<ItemDTO> getAllItems();
    void saveItem(ItemDTO dto);
    ItemDTO getItemByCode(Long code);
    void updateItemWithCode(ItemDTO dto, Long code) throws ChangeSetPersister.NotFoundException;
}
