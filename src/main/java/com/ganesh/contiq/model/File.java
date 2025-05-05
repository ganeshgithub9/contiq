package com.ganesh.contiq.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "file")
public class File {
    @Id
    String id;
    String name;
    String content;
    String userId;
}