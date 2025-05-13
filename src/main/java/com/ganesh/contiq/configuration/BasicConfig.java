package com.ganesh.contiq.configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganesh.contiq.DTO.FileMetaDataDTO;
import com.ganesh.contiq.model.File;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@Configuration
public class BasicConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper=new ModelMapper();

        TypeMap<File, FileMetaDataDTO> typeMap = modelMapper.createTypeMap(File.class, FileMetaDataDTO.class);

        typeMap.addMapping(File::getId, FileMetaDataDTO::setFileId);
        typeMap.addMapping(File::getName, FileMetaDataDTO::setName);
        return modelMapper;
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate(ElasticsearchClient client) {
        return new ElasticsearchTemplate(client);
    }

    @Bean
    public ObjectMapper getObjectMapper(){
        return new ObjectMapper();
    }

}
