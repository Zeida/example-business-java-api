package com.example.business.api.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="items")
public class Item {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "items_seq")
    @SequenceGenerator(name = "items_seq", sequenceName = "items_id_seq")
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    private Long code;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", columnDefinition = "varchar(25) default 'ACTIVE'")
    private ItemStateEnum state = ItemStateEnum.ACTIVE;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "item_supplier",
            joinColumns = { @JoinColumn(name = "item_id") },
            inverseJoinColumns = { @JoinColumn(name = "supplier_id") }
    )
    private Set<Supplier> suppliers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = true)
    private List<PriceReduction> priceReductions;

    @Column(name = "creation_date", columnDefinition = "timestamp default current_timestamp()")
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    public Item() {
    }

    public Item(Long code, Double price, ItemStateEnum state, User creator) {
        this.code = code;
        this.price = price;
        this.state = state;
        this.creator = creator;
    }

    public Item(Long code, String description, Double price, ItemStateEnum state, Set<Supplier> suppliers, List<PriceReduction> priceReductions, LocalDateTime creationDate, User creator) {
        this.code = code;
        this.description = description;
        this.price = price;
        this.state = state;
        this.suppliers = suppliers;
        this.priceReductions = priceReductions;
        this.creationDate = creationDate;
        this.creator = creator;
    }

    public Item(Long id, Long code, String description, Double price, ItemStateEnum state, Set<Supplier> suppliers, List<PriceReduction> priceReductions, LocalDateTime creationDate, User creator) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.price = price;
        this.state = state;
        this.suppliers = suppliers;
        this.priceReductions = priceReductions;
        this.creationDate = creationDate;
        this.creator = creator;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public ItemStateEnum getState() {
        return state;
    }

    public void setState(ItemStateEnum state) {
        this.state = state;
    }

    public Set<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(Set<Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    public List<PriceReduction> getPriceReductions() {
        return priceReductions;
    }

    public void setPriceReductions(List<PriceReduction> priceReductions) {
        this.priceReductions = priceReductions;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void addSupplier(Supplier supplier) {
        if(suppliers == null) {
            suppliers = new HashSet<>();
        }
        supplier.addItem(this);
        suppliers.add(supplier);
    }

    public void addPriceReduction(PriceReduction priceReduction) {
        if(priceReductions == null) {
            priceReductions = new ArrayList<>();
        }
        priceReduction.setItem(this);
        priceReductions.add(priceReduction);
    }
}
