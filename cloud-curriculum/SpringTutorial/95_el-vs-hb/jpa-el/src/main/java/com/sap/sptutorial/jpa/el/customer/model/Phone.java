package com.sap.sptutorial.jpa.el.customer.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.ReadTransformer;
import org.eclipse.persistence.annotations.Transformation;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = Phone.TABLE_NAME)
public class Phone {
	public static final String TABLE_NAME = "PHONE";
	public static final String SEQUENCE_NAME = "S_PHONE";
	public static final String OWNER_COLUMN = "OWNER";
	public static final String PHONE_TYPE_COLUMN = "PHONE_TYPE";
	public static final String NUMBER_COLUMN = "NUMBER";

	enum PhoneType {
		MOBILE, HOME, OFFICE
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_phone_number")
	@SequenceGenerator(name = "s_phone_number", sequenceName = SEQUENCE_NAME, allocationSize = 50)
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = OWNER_COLUMN)
	private Customer owner;

	@Transformation(fetch = FetchType.EAGER)
	@ReadTransformer(transformerClass = CustomerNameTransformer.class)
	private String customerName;
	
	@Transient
	private String customerNameTransient;
	
	public String getCustomerNameTransient() {
		return owner.getDisplayName();
	}

	@Enumerated(EnumType.STRING)
	@Column(name = PHONE_TYPE_COLUMN)
	private PhoneType phoneType;

	@NotEmpty
	@Column(name = NUMBER_COLUMN)
	private String number;

	@Version
	private Long version;
}
