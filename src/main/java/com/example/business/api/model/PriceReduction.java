package com.example.business.api.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_reductions")
public class PriceReduction {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "price_reductions_seq")
    @SequenceGenerator(name = "price_reductions_seq", sequenceName = "price_reductions_id_seq")
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private Long code;

    @Column(name = "amount_deducted", nullable = false)
    private Double amountDeducted;

    @Column(name = "start_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP()")
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    public PriceReduction() {
    }

    public PriceReduction(Long code, Double amountDeducted, LocalDateTime endDate, Item item) {
        this.code = code;
        this.amountDeducted = amountDeducted;
        this.endDate = endDate;
        this.item = item;
    }

    public PriceReduction(Long code, Double amountDeducted, LocalDateTime startDate, LocalDateTime endDate, Item item) {
        this.code = code;
        this.amountDeducted = amountDeducted;
        this.startDate = startDate;
        this.endDate = endDate;
        this.item = item;
    }

    public PriceReduction(Long id, Long code, Double amountDeducted, LocalDateTime startDate, LocalDateTime endDate, Item item) {
        this.id = id;
        this.code = code;
        this.amountDeducted = amountDeducted;
        this.startDate = startDate;
        this.endDate = endDate;
        this.item = item;
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
