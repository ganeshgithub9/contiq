package com.ganesh.contiq.configuration;

import com.ganesh.contiq.DTO.FileMetaDataDTO;
import com.ganesh.contiq.model.File;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

}
