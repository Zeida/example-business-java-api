package com.example.business.api.dto;

import com.example.business.api.model.ItemStateEnum;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemDTO implements Serializable {
    private Long id;

    private Long code;

    private String description;

    private Double price;

    private ItemStateEnum state;

    private Set<SupplierDTO> suppliers;

    private List<PriceReductionDTO> priceReductions;

    private List<DeactivationReasonDTO> deactivationReasons;

    private LocalDateTime creationDate;

    private UserDTO creator;

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

    public Set<SupplierDTO> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(Set<SupplierDTO> suppliers) {
        this.suppliers = suppliers;
    }

    public List<PriceReductionDTO> getPriceReductions() {
        return priceReductions;
    }

    public void setPriceReductions(List<PriceReductionDTO> priceReductions) {
        this.priceReductions = priceReductions;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public UserDTO getCreator() {
        return creator;
    }

    public void setCreator(UserDTO creator) {
        this.creator = creator;
    }

    public void addItem(SupplierDTO supplier) {
        if(suppliers == null) {
            suppliers = new HashSet<>();
        }
        supplier.addItem(this);
        suppliers.add(supplier);
    }

    public void addPriceReduction(PriceReductionDTO priceReduction) {
        if(priceReductions == null) {
            priceReductions = new ArrayList<>();
        }
        priceReduction.setItem(this);
        priceReductions.add(priceReduction);
    }

    public void addDeactivationReason(DeactivationReasonDTO deactivationReason) {
        if(deactivationReasons == null) {
            deactivationReasons = new ArrayList<>();
        }
        deactivationReason.setItem(this);
        deactivationReasons.add(deactivationReason);
    }
}
