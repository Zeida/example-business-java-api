package com.example.business.api.repository;

import com.example.business.api.model.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface ItemRepository extends CrudRepository<Item, Long> {
    Set<Item> findByCode(Long code);
}
