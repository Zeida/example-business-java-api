package com.example.business.api.repository;

import com.example.business.api.model.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {
    Optional<Item> findByCode(Long code);

    @Query(nativeQuery = true, value = "" +
            "select * from items where price " +
            "in (select min(price) from item_supplier as it_s " +
            "inner join suppliers as s on (it_s.supplier_id = s.id) " +
            "inner join items as i on (i.id = it_s.item_id) " +
            "group by s.id);")
    Iterable<Item> findCheapestItemPerSupplier();
}
