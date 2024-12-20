package com.example.java_app.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "custom_subs")
public class CustomSubs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "duration_months", nullable = false)
    private int durationMonths;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "user_card_id", nullable = false)
    private UserCard userCard;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    public CustomSubs() {

    }

    public CustomSubs(Long id, AppUser user, String serviceName, int durationMonths, BigDecimal price,
                      UserCard userCard,
                      LocalDate date) {
        this.id = id;
        this.user = user;
        this.serviceName = serviceName;
        this.durationMonths = durationMonths;
        this.price = price;
        this.userCard = userCard;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public UserCard getUserCard() {
        return userCard;
    }

    public void setUserCard(UserCard userCard) {
        this.userCard = userCard;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
