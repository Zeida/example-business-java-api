package com.example.business.api.repository;

import com.example.business.api.model.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {
    Optional<Item> findByCode(Long code);
}
