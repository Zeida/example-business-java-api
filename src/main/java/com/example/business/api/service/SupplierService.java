package com.example.business.api.service;

import com.example.business.api.dto.SupplierDTO;
import com.example.business.api.model.Supplier;

public interface SupplierService extends BaseService<Supplier, SupplierDTO> {
    Iterable<SupplierDTO> getAllSuppliers();
    Void saveSupplier(SupplierDTO dto);
    SupplierDTO getSupplierByName(String name);
    Void updateSupplierWithName(SupplierDTO dto, String name);
    Iterable<SupplierDTO> findSuppliersWhoseItemsHasPriceReductions();
}
