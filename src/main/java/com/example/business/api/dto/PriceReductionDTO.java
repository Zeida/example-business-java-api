package com.example.business.api.dto;

import java.time.LocalDateTime;

public class PriceReductionDTO {
    private Long id;

    private Double amountDeducted;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private ItemDTO item;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmountDeducted() {
        return amountDeducted;
    }

    public void setAmountDeducted(Double amountDeducted) {
        this.amountDeducted = amountDeducted;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public ItemDTO getItem() {
        return item;
    }

    public void setItem(ItemDTO item) {
        this.item = item;
    }
}
