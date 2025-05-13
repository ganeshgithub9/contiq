package com.ganesh.contiq.DTO;

import com.ganesh.contiq.model.Paragraph;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ParagraphListDTO {
    List<Paragraph> paragraphList;
}
