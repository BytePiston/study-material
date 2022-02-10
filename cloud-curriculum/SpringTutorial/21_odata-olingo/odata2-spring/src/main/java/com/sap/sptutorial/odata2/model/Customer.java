package com.sap.sptutorial.odata2.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sap.icd.odatav2.spring.annotations.ModelProperty;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name="CUSTOMER")
public class Customer {
    @Id
    private Long id;

    @Column(name="DISPLAY_NAME")
    private String displayName;

    @Column(name="DATE_OF_BIRTH")
    private Date dateOfBirth;
    
    @Column(name="PHONE_NUMBER")
    @ModelProperty.IsPhoneNumber
    private String phoneNumber;
}
