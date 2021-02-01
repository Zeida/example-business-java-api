package com.example.business.api.controller;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class ItemController {
    @Autowired
    private ItemService itemService;

    @GetMapping(path = "/items")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public Iterable<ItemDTO> allItems() {
        return itemService.getAllItems();
    }

    @GetMapping(path = "/items/cheapest")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public Iterable<ItemDTO> cheapestItemsBySupplier() {
        return itemService.findCheapestItemPerSupplier();
    }

    @PostMapping(path = "/items", consumes = "application/json")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public void addItem(@RequestBody ItemDTO item) {
        itemService.saveItem(item);
    }

    @GetMapping(path = "/items/{code}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public ItemDTO getItemByCode(@PathVariable Long code) {
        return itemService.getItemByCode(code);
    }

    @PutMapping(path = "/items/{code}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public void updateItemByCode(@PathVariable Long code, @RequestBody ItemDTO item) {
        itemService.updateItemWithCode(item, code);
    }

    @PutMapping(path = "/items/deactivate/{code}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public void deactivateItem(@PathVariable Long code) {
        itemService.deactivateItem(code);
    }

    @DeleteMapping(path = "/items")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteItem(@RequestBody ItemDTO item) {
        itemService.deleteItem(item);
    }

}
