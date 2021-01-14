package com.example.business.api.service;

import com.example.business.api.dto.SupplierDTO;
import com.example.business.api.model.Supplier;
import org.springframework.data.crossstore.ChangeSetPersister;

public interface SupplierService extends BaseService<Supplier, SupplierDTO> {
    Iterable<SupplierDTO> getAllSuppliers();
    void saveSupplier(SupplierDTO dto);
    SupplierDTO getSupplierByName(String name);
    void updateSupplierWithName(SupplierDTO dto, String name) throws ChangeSetPersister.NotFoundException;
}
