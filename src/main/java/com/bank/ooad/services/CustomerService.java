package com.bank.ooad.services;

import com.bank.ooad.models.users.Customer;
import com.bank.ooad.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final UserRepository userRepository;

    public CustomerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Customer registerCustomer(String name, String email, double income, String password) {
        // Check for duplicate email
        boolean emailExists = userRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
        if (emailExists) {
            throw new RuntimeException("An account with this email already exists.");
        }
        Customer c = new Customer();
        c.setName(name);
        c.setEmail(email);
        c.setIncome(income);
        c.setRole("USER");
        c.setKycStatus("PENDING");
        c.setCreditScore(650); // Fixed initial score as per architectural plan
        c.setPasswordHash(hashPassword(password));
        c.register();
        return userRepository.save(c);
    }

    public boolean verifyPassword(String rawPassword, String storedHash) {
        return hashPassword(rawPassword).equals(storedHash);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    public Customer getCustomer(String id) {
        return (Customer) userRepository.findById(id).orElse(null);
    }

    public List<Customer> getAllCustomers() {
        return userRepository.findAll().stream()
                .filter(u -> u instanceof Customer)
                .map(u -> (Customer) u)
                .collect(Collectors.toList());
    }
}
