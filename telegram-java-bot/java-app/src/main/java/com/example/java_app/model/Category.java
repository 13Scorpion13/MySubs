package com.example.java_app.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @OneToMany(mappedBy = "category")
    private List<Subs> subs;

    public Category() {

    }

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<Subs> getSubs() {
        return subs;
    }

    public void setSubs(List<Subs> subs) {
        this.subs = subs;
    }
}
