package com.example.business.api.controller;

import com.example.business.api.dto.SupplierDTO;
import com.example.business.api.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

    @GetMapping(path = "/suppliers")
    @ResponseBody
    public Iterable<SupplierDTO> allSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @PostMapping(path = "/suppliers", consumes = "application/json")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void addSupplier(@RequestBody SupplierDTO supplier) {
        supplierService.saveSupplier(supplier);
    }

    @GetMapping(path = "/suppliers/{name}")
    @ResponseBody
    public SupplierDTO getSupplierByName(@PathVariable String name) {
        return supplierService.getSupplierByName(name);
    }

    @PutMapping(path = "/suppliers/{name}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateSupplierByName(@PathVariable String name, @RequestBody SupplierDTO item) throws ChangeSetPersister.NotFoundException {
        supplierService.updateSupplierWithName(item, name);
    }
}
