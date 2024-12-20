package com.example.java_app.model;


import jakarta.persistence.*;
import java.util.List;


@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_id", unique = true, nullable = false)
    private Long telegramId;

    @Column(name="nickname")
    private String nickname;

    @Column(name="consent", nullable = false)
    private boolean consent = false;

    @OneToMany(mappedBy = "user")
    private List<Subs> subs;

    @OneToMany(mappedBy = "user")
    private List<UserCard> userCards;

    @OneToMany(mappedBy = "user")
    private List<CustomSubs> customSubs;


    public AppUser() {}

    public AppUser(Long telegramId, String nickname, boolean consent) {
        this.telegramId = telegramId;
        this.nickname = nickname;
        this.consent = consent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isConsent() {
        return consent;
    }

    public void setConsent(boolean consent) {
        this.consent = consent;
    }

    public List<Subs> getSubs() {
        return subs;
    }

    public void setSubs(List<Subs> subs) {
        this.subs = subs;
    }

    public List<UserCard> getUserCards() {
        return userCards;
    }

    public void setUserCards(List<UserCard> userCards) {
        this.userCards = userCards;
    }

    public List<CustomSubs> getCustomSubs() {
        return customSubs;
    }

    public void setCustomSubs(List<CustomSubs> customSubs) {
        this.customSubs = customSubs;
    }
}