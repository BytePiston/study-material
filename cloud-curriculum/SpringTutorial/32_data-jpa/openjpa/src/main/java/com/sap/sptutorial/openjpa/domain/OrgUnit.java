package com.sap.sptutorial.openjpa.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.StringJoiner;

@Slf4j
@Entity
@Table(name = "ORGUNIT")
public class OrgUnit {
  @Id
  @Getter
  @Setter
  private Long id;

  @Getter
  @Version
  private Long version;

  @Getter
  @Setter
  private String name;

  @Getter
  @Column(name = "CREATED_AT", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP()")
  private Timestamp createdAt;

  @Getter
  @Column(name = "MODIFIED_AT")
  private Timestamp modifiedAt;

  @Getter
  @OneToMany(mappedBy = "orgUnit")
  @JsonIgnore
  private Collection<Employee> employees;

  protected OrgUnit() {
  }

  @Override
  public String toString() {
    return new StringJoiner(", ").add(String.valueOf(id)).add(name).toString();
  }

  @PreUpdate
  public void setModificationTime() {
    modifiedAt = Timestamp.from(Instant.now());
  }

  @PostUpdate
  public void logUpdate() {
    log.debug("OrgUnit [{}] has been updated at {}", id, Instant.now());
  }
}
