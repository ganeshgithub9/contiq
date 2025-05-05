package com.ganesh.contiq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "paragraph")
public class Paragraph {

    @Id
    private String id;
    private String content;
    private int pageNumber;
    private String fileId;
    private int paragraphNumber;
}

