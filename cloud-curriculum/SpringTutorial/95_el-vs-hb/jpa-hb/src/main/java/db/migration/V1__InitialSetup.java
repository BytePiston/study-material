package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.sap.sptutorial.jpa.hb.customer.model.Customer;
import com.sap.sptutorial.jpa.hb.customer.model.Phone;


public class V1__InitialSetup implements SpringJdbcMigration {
	private final String TABLE_HEADER = "ID bigint primary key, VERSION bigint,";

	@Override
	public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
		jdbcTemplate.execute(String.format(
				"create sequence %s start with %d increment by %d",
				Phone.SEQUENCE_NAME, 50, 50));
		
		jdbcTemplate.execute(String.format(
				"create table %s (%s %s bigint, %s VARCHAR(32), %s VARCHAR(128))",
				Phone.TABLE_NAME, TABLE_HEADER, Phone.OWNER_COLUMN,
				Phone.PHONE_TYPE_COLUMN, Phone.NUMBER_COLUMN));

		jdbcTemplate.execute(String.format(
				"create sequence %s start with %d increment by %d", Customer.SEQUENCE_NAME, 50, 50));
		
		jdbcTemplate.execute(String.format("create table %s (%s %s VARCHAR(3000))", Customer.TABLE_NAME,
				TABLE_HEADER, Customer.DISPLAY_NAME_COLUMN));
	}
}
