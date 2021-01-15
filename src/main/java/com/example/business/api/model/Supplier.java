package com.example.business.api.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "suppliers_seq")
    @SequenceGenerator(name = "suppliers_seq", sequenceName = "suppliers_id_seq")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "country")
    private String country;

    @ManyToMany(mappedBy = "suppliers")
    private Set<Item> items;

    public Supplier() {
    }

    public Supplier(String name, String country, Set<Item> items) {
        this.name = name;
        this.country = country;
        this.items = items;
    }

    public Supplier(Long id, String name, String country, Set<Item> items) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }
}
