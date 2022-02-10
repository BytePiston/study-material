package com.sap.sptutorial.rest.customer;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "s_customer")
	@SequenceGenerator(name = "s_customer", sequenceName = "S_CUSTOMER")
	private Long id;

	@NotNull
	private String displayName;

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	private List<Phone> phones;
	
	@Version
	private Long version;
}
