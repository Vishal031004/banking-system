package com.bank.ooad.services;

import com.bank.ooad.models.users.Customer;
import com.bank.ooad.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final UserRepository userRepository;

    public CustomerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Customer registerCustomer(String name, String email, double income) {
        Customer c = new Customer();
        c.setName(name);
        c.setEmail(email);
        c.setIncome(income);
        c.setRole("USER");
        c.setKycStatus("PENDING");
        c.setCreditScore(650); // Fixed initial score as per architectural plan
        c.register();
        return userRepository.save(c);
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
