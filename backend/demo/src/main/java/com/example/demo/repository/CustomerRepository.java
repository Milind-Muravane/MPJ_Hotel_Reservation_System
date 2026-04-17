package com.example.demo.repository;

import com.example.demo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // 🔐 Login using email + password
    Optional<Customer> findByEmailAndPassword(String email, String password);
}