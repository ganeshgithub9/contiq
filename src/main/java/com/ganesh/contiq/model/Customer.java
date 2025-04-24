package com.ganesh.contiq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "customers")
public class Customer {

    @Id
    private Long id;
    private String firstName,lastName;
    private int age;
}
