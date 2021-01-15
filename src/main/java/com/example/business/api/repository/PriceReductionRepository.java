package com.example.business.api.repository;

import com.example.business.api.model.PriceReduction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceReductionRepository extends CrudRepository<PriceReduction, Long> {
    Optional<PriceReduction> findByCode(Long code);
}
