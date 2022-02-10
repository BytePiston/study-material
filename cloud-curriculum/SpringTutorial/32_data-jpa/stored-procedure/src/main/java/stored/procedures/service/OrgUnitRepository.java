package stored.procedures.service;

import org.springframework.data.jpa.repository.JpaRepository;
import stored.procedures.domain.OrgUnit;

public interface OrgUnitRepository extends JpaRepository<OrgUnit, Long> {
}
