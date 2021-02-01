package com.example.business.api.controller;

import com.example.business.api.dto.SupplierDTO;
import com.example.business.api.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

    @GetMapping(path = "/suppliers")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public Iterable<SupplierDTO> allSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @GetMapping(path = "/suppliers/with-items/with-reductions")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public Iterable<SupplierDTO> suppliersWhoseItemsHasReductions() {
        return supplierService.findSuppliersWhoseItemsHasPriceReductions();
    }

    @PostMapping(path = "/suppliers", consumes = "application/json")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public void addSupplier(@RequestBody SupplierDTO supplier) {
        supplierService.saveSupplier(supplier);
    }

    @GetMapping(path = "/suppliers/{name}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public SupplierDTO getSupplierByName(@PathVariable String name) {
        return supplierService.getSupplierByName(name);
    }

    @PutMapping(path = "/suppliers/{name}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_ADMIN')")
    public void updateSupplierByName(@PathVariable String name, @RequestBody SupplierDTO item) {
        supplierService.updateSupplierWithName(item, name);
    }
}
