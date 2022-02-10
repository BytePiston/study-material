package com.sap.icd.odata4.example.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Customer {
    @Id
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String displayName;

    @Getter
    @Setter
    private Date dateOfBirth;
}
