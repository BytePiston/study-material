package com.sap.sptutorial.validation.model;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
@Setter
public class DealCategory {
    @NotNull
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String key;
}
