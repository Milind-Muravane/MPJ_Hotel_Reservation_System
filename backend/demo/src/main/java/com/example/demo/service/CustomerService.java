package com.example.demo.service;

import com.example.demo.entity.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // ✅ Get all customers
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // ✅ Get customer by ID
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    // ✅ Register
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // ✅ Delete
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    // 🔐 LOGIN
    public Customer login(Customer customer) {
        return customerRepository
                .findByEmailAndPassword(customer.getEmail(), customer.getPassword())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
    }
}