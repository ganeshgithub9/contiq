package com.ganesh.contiq.controller;

import com.ganesh.contiq.model.Customer;
import com.ganesh.contiq.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;

@RestController
public class CustomerController {

    CustomerRepository customerRepository;

    CustomerController(@Autowired CustomerRepository customerRepository){
        this.customerRepository=customerRepository;
    }

    @PostMapping("/api/v1/customers")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer){
        return new ResponseEntity<>(customerRepository.save(customer), HttpStatus.CREATED);
    }

    @GetMapping("/api/v1/customers")
    public ResponseEntity<Iterable<Customer>> getCustomers(){
        return new ResponseEntity<>(customerRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/api/v1/customers?first-name={firstName}")
    public ResponseEntity<Iterable<Customer>> getCustomersByFirstName(@RequestParam String firstName){
        return new ResponseEntity<>(customerRepository.findAllByFirstName(), HttpStatus.OK);
    }
}
