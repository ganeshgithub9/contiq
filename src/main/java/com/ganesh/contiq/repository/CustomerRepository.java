package com.ganesh.contiq.repository;

import com.ganesh.contiq.model.Customer;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends ElasticsearchRepository<Customer,Long> {
    Iterable<Customer> findAllByFirstName();
}
