package com.example.business.api.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_reductions")
public class PriceReduction {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "price_reductions_seq")
    @SequenceGenerator(name = "price_reductions_seq", sequenceName = "price_reductions_id_seq")
    private Long id;

    @Column(name = "amount_deducted")
    private Double amountDeducted;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
