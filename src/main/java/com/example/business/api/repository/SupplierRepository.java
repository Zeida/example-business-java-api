package com.example.business.api.repository;

import com.example.business.api.model.Supplier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SupplierRepository extends CrudRepository<Supplier, Long> {
    Optional<Supplier> findByName(String name);
    Iterable<Supplier> findByCountry(String country);

    @Query("SELECT s FROM Supplier s" +
            " JOIN s.items i " +
            "WHERE i.priceReductions IS NOT EMPTY")
    Iterable<Supplier> findSuppliersWhoseItemsHasPriceReductions();
}
