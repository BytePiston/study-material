package com.sap.sptutorial.unittesting.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.StringJoiner;

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
