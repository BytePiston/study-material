package com.sap.sptutorial.jpa.hb.customer.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.rest.core.annotation.RestResource;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer {
	public static final String TABLE_NAME = "CUSTOMER";
	public static final String SEQUENCE_NAME = "S_CUSTOMER";
	public static final String DISPLAY_NAME_COLUMN = "DISPLAY_NAME";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,
			generator = "s_customer")
	@SequenceGenerator(name = "s_customer", sequenceName = "S_CUSTOMER",
			allocationSize = 50)
	private Long id;

	@NotEmpty
	@Column(name = "DISPLAY_NAME")
	private String displayName;

	@OneToMany(mappedBy = "owner")
	private List<Phone> phones;

	@Version
	private Long version;
}
