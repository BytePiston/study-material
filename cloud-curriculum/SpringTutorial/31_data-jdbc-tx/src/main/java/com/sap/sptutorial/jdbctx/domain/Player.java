package com.sap.sptutorial.jdbctx.domain;

import lombok.Getter;
import lombok.ToString;
import java.sql.Timestamp;

@ToString
public class Player {
  @Getter
  private int id;
  @Getter
  private String name;
  @Getter
  private Timestamp birthDate;

  public Player(int id, String name, Timestamp birthDate) {
    this.id = id;
    this.name = name;
    this.birthDate = birthDate;
  }

  public Player() {
  }
}
