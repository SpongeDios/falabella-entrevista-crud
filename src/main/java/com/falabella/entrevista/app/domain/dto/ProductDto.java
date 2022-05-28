package com.falabella.entrevista.app.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.net.URI;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDto {
    private String name;
    private String brand;
    private String size;
    private Double price;
    private String principalImage;
    private List<String> otherImages;
}
