package db.migration;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.sap.sptutorial.jpa.el.customer.model.Customer;
import com.sap.sptutorial.jpa.el.customer.model.Phone;

public class V1_02__InsertInitialData implements SpringJdbcMigration {
	private final String TABLE_HEADER = "ID, VERSION";

	@Override
	public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
		insertCustomerData(jdbcTemplate);
		insertPhoneData(jdbcTemplate);
	}

	private void insertPhoneData(JdbcTemplate jdbcTemplate) {
		String sqlStmt = String.format("insert into %s (%s, %s, %s, %s)  select nextval('%s'), ?, ?, ?, ?", Phone.TABLE_NAME,
				TABLE_HEADER, Phone.PHONE_TYPE_COLUMN, Phone.NUMBER_COLUMN, Phone.OWNER_COLUMN, Phone.SEQUENCE_NAME);
		jdbcTemplate.batchUpdate(sqlStmt, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, 0);
				ps.setString(2, "MOBILE");
				ps.setString(3, "4711-" + i);
				ps.setInt(4, 50 * (i + 1));
			}

			@Override
			public int getBatchSize() {
				return 10000;
			}
		});
	}

	private void insertCustomerData(JdbcTemplate jdbcTemplate) {
		String sqlStmt = String.format("insert into %s (%s, %s)  select nextval('%s'), ?, ?", Customer.TABLE_NAME,
				TABLE_HEADER, Customer.DISPLAY_NAME_COLUMN, Customer.SEQUENCE_NAME);
		jdbcTemplate.batchUpdate(sqlStmt, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, 0);
				ps.setString(2, "SAP-SE");
			}

			@Override
			public int getBatchSize() {
				return 10000;
			}
		});
	}
}
