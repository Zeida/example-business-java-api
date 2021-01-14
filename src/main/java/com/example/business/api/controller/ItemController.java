package com.example.business.api.controller;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class ItemController {
    @Autowired
    private ItemService itemService;

    @GetMapping(path = "/items")
    @ResponseBody
    public Iterable<ItemDTO> allItems() {
        return itemService.getAllItems();
    }

    @PostMapping(path = "/items", consumes = "application/json")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void addItem(@RequestBody ItemDTO item) throws ChangeSetPersister.NotFoundException {
        itemService.saveItem(item);
    }

    @GetMapping(path = "/items/{code}")
    @ResponseBody
    public ItemDTO getItemByCode(@PathVariable Long code) {
        return itemService.getItemByCode(code);
    }

    @PutMapping(path = "/items/{code}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateItemByCode(@PathVariable Long code, @RequestBody ItemDTO item) throws ChangeSetPersister.NotFoundException {
        itemService.updateItemWithCode(item, code);
    }
}
