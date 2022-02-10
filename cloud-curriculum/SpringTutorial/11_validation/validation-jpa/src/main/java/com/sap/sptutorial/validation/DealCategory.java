package com.sap.sptutorial.validation;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class DealCategory {

    @NotNull
    @Id
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String key;
}
