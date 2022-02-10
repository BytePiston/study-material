package com.sap.sptutorial.datarest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collection;
import java.util.StringJoiner;

@Entity
@Table(name = "ORGUNIT")
public class OrgUnit {
  @Id
  @Getter
  @Setter
  private Long id;

  @Getter
  private String name;

  @Getter
  @OneToMany(mappedBy = "orgUnit")
  @JsonIgnore
  private Collection<Employee> employees;

  protected OrgUnit() {
  }

  public OrgUnit(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ").add(String.valueOf(id)).add(name).toString();
  }
}
