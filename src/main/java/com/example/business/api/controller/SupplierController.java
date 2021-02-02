package com.example.business.api.controller;

import com.example.business.api.dto.SupplierDTO;
import com.example.business.api.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.WebAsyncTask;

@RestController
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

    @GetMapping(path = "/suppliers")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public WebAsyncTask<Iterable<SupplierDTO>> allSuppliers() {
        return new WebAsyncTask<>(() -> supplierService.getAllSuppliers());
    }

    @GetMapping(path = "/suppliers/with-items/with-reductions")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public WebAsyncTask<Iterable<SupplierDTO>> suppliersWhoseItemsHasReductions() {
        return new WebAsyncTask<>(() -> supplierService.findSuppliersWhoseItemsHasPriceReductions());
    }

    @PostMapping(path = "/suppliers", consumes = "application/json")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public WebAsyncTask<Void> addSupplier(@RequestBody SupplierDTO supplier) {
        return new WebAsyncTask<>(() -> supplierService.saveSupplier(supplier));
    }

    @GetMapping(path = "/suppliers/{name}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public WebAsyncTask<SupplierDTO> getSupplierByName(@PathVariable String name) {
        return new WebAsyncTask<>(() -> supplierService.getSupplierByName(name));
    }

    @PutMapping(path = "/suppliers/{name}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public WebAsyncTask<Void> updateSupplierByName(@PathVariable String name, @RequestBody SupplierDTO item) {
        return new WebAsyncTask<>(() -> supplierService.updateSupplierWithName(item, name));
    }
}
