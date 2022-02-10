package com.sap.sptutorial.jndi;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ds")
public class JndiController {
	private final DataSource dataSource;
	
	@Autowired
	public JndiController(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@GetMapping
	public String ping() {
		return "pong";
	}
	
	@GetMapping("schema")
	public String getSchema() throws SQLException {
		return dataSource.getConnection().getSchema();
	}
}
