package stored.procedures.domain;

import lombok.Getter;
import javax.persistence.*;

@Entity
@Table(name = "ORGUNIT")
@NamedStoredProcedureQuery(name = "POWER", procedureName = "JAVA_POWER",
        parameters = {
                @StoredProcedureParameter(name = "base", mode = ParameterMode.IN, type = Double.class)
                , @StoredProcedureParameter(name = "exponent", mode = ParameterMode.IN, type = Double.class)
                //, @StoredProcedureParameter(name = "result", mode = ParameterMode.OUT, type = Double.class)
        })
public class OrgUnit {
  @Id
  @Getter
  private Long id;
  @Getter
  private String name;

  protected OrgUnit() {
  }

  public OrgUnit(String name) {
    this.name = name;
  }

}
