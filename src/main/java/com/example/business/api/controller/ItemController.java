package com.example.business.api.controller;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.WebAsyncTask;

@RestController
public class ItemController {
    @Autowired
    private ItemService itemService;

    @GetMapping(path = "/items")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public WebAsyncTask<Iterable<ItemDTO>> allItems() {
        return new WebAsyncTask<>(() -> itemService.getAllItems());
    }

    @GetMapping(path = "/items/cheapest")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public WebAsyncTask<Iterable<ItemDTO>> cheapestItemsBySupplier() {
        return new WebAsyncTask<>(() -> itemService.findCheapestItemPerSupplier());
    }

    @PostMapping(path = "/items", consumes = "application/json")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public WebAsyncTask<Void> addItem(@RequestBody ItemDTO item) {
        return new WebAsyncTask<>(() -> itemService.saveItem(item));
    }

    @GetMapping(path = "/items/{code}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public WebAsyncTask<ItemDTO> getItemByCode(@PathVariable Long code) {
        return new WebAsyncTask<>(() -> itemService.getItemByCode(code));
    }

    @PutMapping(path = "/items/{code}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public WebAsyncTask<Void> updateItemByCode(@PathVariable Long code, @RequestBody ItemDTO item) {
        return new WebAsyncTask<>(() -> itemService.updateItemWithCode(item, code));
    }

    @PutMapping(path = "/items/deactivate/{code}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public WebAsyncTask<Void> deactivateItem(@PathVariable Long code) {
        return new WebAsyncTask<>(() -> itemService.deactivateItem(code));
    }

    @DeleteMapping(path = "/items")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public WebAsyncTask<Void> deleteItem(@RequestBody ItemDTO item) {
        return new WebAsyncTask<>(() -> itemService.deleteItem(item));
    }

}
