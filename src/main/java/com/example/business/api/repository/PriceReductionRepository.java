package com.example.business.api.repository;

import com.example.business.api.model.PriceReduction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceReductionRepository extends CrudRepository<PriceReduction, Long> {
}
