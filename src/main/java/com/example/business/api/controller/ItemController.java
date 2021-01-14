package com.example.business.api.controller;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.model.Item;
import com.example.business.api.service.ItemService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

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
    @ResponseStatus(code = HttpStatus.CREATED)
    public void addItem(@RequestBody ItemDTO item) {
        itemService.addItem(item);
    }
}
