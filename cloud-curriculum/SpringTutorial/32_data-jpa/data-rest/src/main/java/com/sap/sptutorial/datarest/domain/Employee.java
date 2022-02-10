package com.sap.sptutorial.datarest.domain;

import java.util.StringJoiner;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Employee {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  private Long id;

  @Getter
  @Column(name = "FIRSTNAME")
  private String firstName;

  @Getter
  @Column(name = "LASTNAME", nullable = false)
  private String lastName;

  @Getter
  @Column(nullable = false)
  private String email;

  @Getter
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "ORGUNIT_ID")
  private OrgUnit orgUnit;

  protected Employee() {
  }

  public Employee(String firstName, String lastName, String email) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
  }

  @Override
  public String toString() {
    return new StringJoiner(",").add(firstName).add(lastName).add(email).add(orgUnit.getName()).toString();
  }
}
