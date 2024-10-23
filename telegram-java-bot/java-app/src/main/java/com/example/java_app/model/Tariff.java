package com.example.java_app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "tariff")
public class Tariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "duration_months", nullable = false)
    private int durationMonths;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @OneToMany(mappedBy = "tariff")
    private List<Subs> subs;

    public Tariff() {

    }

    public Tariff(int durationMonths, BigDecimal price) {
        this.durationMonths = durationMonths;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<Subs> getSubs() {
        return subs;
    }

    public void setSubs(List<Subs> subs) {
        this.subs = subs;
    }
}
