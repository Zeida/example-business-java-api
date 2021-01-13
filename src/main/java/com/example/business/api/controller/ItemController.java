package com.example.business.api.controller;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.model.Item;
import com.example.business.api.service.ItemService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
public class ItemController {
    @Autowired
    private ItemService itemService;

    @GetMapping(name = "items", path = "/items")
    @ResponseBody
    public Iterable<ItemDTO> allItems() {
        return itemService.getAllItems();
    }
}
