package com.example.business.api.controller;

import com.example.business.api.dto.ItemDTO;
import com.example.business.api.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @PostMapping(path = "/items", consumes = "application/json")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public void addItem(@RequestBody ItemDTO item) throws ChangeSetPersister.NotFoundException {
        itemService.saveItem(item);
    }

    @GetMapping(path = "/items/{code}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public ItemDTO getItemByCode(@PathVariable String code) {
        try {
            Long parsedCode = Long.parseLong(code);
            return itemService.getItemByCode(parsedCode);
        } catch(NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The item code provided must be numerical-only", e);
        }
    }

    @PutMapping(path = "/items/{code}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public void updateItemByCode(@PathVariable Long code, @RequestBody ItemDTO item) {
        itemService.updateItemWithCode(item, code);
    }

    @DeleteMapping(path = "/items")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteItem(@RequestBody ItemDTO item) {
        itemService.deleteItem(item);
    }

}
