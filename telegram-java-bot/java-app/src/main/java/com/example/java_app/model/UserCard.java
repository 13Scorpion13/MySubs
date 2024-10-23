package com.example.java_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.List;

@Entity
@Table(name = "users_card")
public class UserCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "card_name", nullable = false)
    private String cardName;

    @Column(name = "last_num")
    private Integer lastNum;

    @OneToMany(mappedBy = "userCard")
    private List<Subs> subs;

    public UserCard(){

    }

    public UserCard(AppUser user, String cardName, Integer lastNum) {
        this.user = user;
        this.cardName = cardName;
        this.lastNum = lastNum;
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

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public Integer getLastNum() {
        return lastNum;
    }

    public void setLastNum(Integer lastNum) {
        this.lastNum = lastNum;
    }

    public List<Subs> getSubs() {
        return subs;
    }

    public void setSubs(List<Subs> subs) {
        this.subs = subs;
    }
}
