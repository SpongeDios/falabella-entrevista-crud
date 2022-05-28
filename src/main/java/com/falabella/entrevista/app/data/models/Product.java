package com.falabella.entrevista.app.data.models;

import com.falabella.entrevista.app.data.generator.StringPrefixedSequenceIdGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @GenericGenerator(
            name = "product_seq",
            strategy = "com.falabella.entrevista.app.data.generator.StringPrefixedSequenceIdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "1"),
                    @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "FAL_"),
                    @org.hibernate.annotations.Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%06d") })
    private String sku;
    @Column
    @NotBlank
    @NotNull
    @Size(min = 3, max = 50)
    private String name;
    @Column
    @NotBlank
    @NotNull
    @Size(min = 3, max = 50)
    private String brand;
    @Column
    @NotBlank
    private String size;
    @Column
    @NotNull
    @Min(1)
    @Max(99999999)
    private Double price;
    @Column
    @NotNull
    private String principalImage;
    @NotNull
    @ElementCollection
    private List<String> otherImages;
    @Column
    @JsonIgnore
    private Boolean status;
}
