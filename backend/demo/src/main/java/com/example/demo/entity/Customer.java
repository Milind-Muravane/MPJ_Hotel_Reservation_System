package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String password;

    // ✅ FIXED: Use Boolean (not boolean) + default value
    @Column(nullable = false)
    private Boolean dnd = false;

    // ✅ Default constructor
    public Customer() {
    }

    // ✅ Constructor WITHOUT id (used for signup)
    public Customer(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.dnd = false;
    }

    // ✅ Full constructor (optional)
    public Customer(Long id, String name, String email, String phone, String password, Boolean dnd) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.dnd = dnd;
    }

    // ✅ GETTERS

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public Boolean getDnd() {
        return dnd;
    }

    // ✅ SETTERS

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDnd(Boolean dnd) {
        this.dnd = dnd;
    }
}