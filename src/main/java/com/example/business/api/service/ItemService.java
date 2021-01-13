package com.example.business.api.service;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.model.Item;

public interface ItemService extends BaseService<Item, ItemDTO>{
    Iterable<ItemDTO> getAllItems();
}
