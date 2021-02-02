package com.example.business.api.model;

import javax.persistence.*;

@Entity
@Table(name = "deactivation_reasons")
public class DeactivationReason {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "deactivation_reason_seq")
    @SequenceGenerator(name = "deactivation_reason_seq", sequenceName = "deactivation_id_seq")
    private Long id;

    @Column(name = "reason", nullable = false)
    private String deactivationReason;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeactivationReason() {
        return deactivationReason;
    }

    public void setDeactivationReason(String deactivationReason) {
        this.deactivationReason = deactivationReason;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
}
