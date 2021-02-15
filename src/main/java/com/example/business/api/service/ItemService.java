package com.example.business.api.service;

import com.example.business.api.dto.DeactivationReasonDTO;
import com.example.business.api.dto.ItemDTO;
import com.example.business.api.model.Item;

public interface ItemService extends BaseService<Item, ItemDTO>{
    Iterable<ItemDTO> getAllItems();
    Void saveItem(ItemDTO dto);
    ItemDTO getItemByCode(Long code);
    Void updateItemWithCode(ItemDTO dto, Long code);
    Void deleteItem(Long code);
    Iterable<ItemDTO> findCheapestItemPerSupplier();
    Void deactivateItem(DeactivationReasonDTO dto, Long code);
}
