package com.sap.sptutorial.hibernate.domain;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.StringJoiner;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.springframework.data.annotation.LastModifiedDate;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@EntityListeners(Employee.EventListener.class)
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Getter
	private Long id;

	@Getter
	@Column(name = "FIRSTNAME")
	private String firstName;

	@Getter
	@Column(name = "LASTNAME", nullable = false)
	private String lastName;

	@Getter
	@Column(nullable = false)
	private String email;

	@Getter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ORGUNIT_ID")
	private OrgUnit orgUnit;

	@LastModifiedDate
	@Column(nullable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP()")
	private Timestamp modifiedAt;

	protected Employee() {
	}

	public Employee(String firstName, String lastName, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	@Override
	public String toString() {
		return new StringJoiner(",").add(firstName).add(lastName).add(email).add(orgUnit.getName()).toString();
	}

	public static final class EventListener {
		@PrePersist
		public void prePersist(Object obj) {
			log.debug("Employee[{}]: prePersist at {}", ((Employee) obj).getId(), Instant.now());
		}

		@PostPersist
		public void postPersist(Object obj) {
			log.debug("Employee[{}]: postPersist at {}", ((Employee) obj).getId(), Instant.now());
		}

		@PreUpdate
		public void preUpdate(Object obj) {
			log.debug("Employee[{}]: preUpdate at {}", ((Employee) obj).getId(), Instant.now());
		}

		@PostUpdate
		public void postUpdate(Object obj) {
			log.debug("Employee[{}]: postUpdate at {}", ((Employee) obj).getId(), Instant.now());
		}

		@PostLoad
		public void postLoad(Object obj) {
			log.debug("Employee[{}]: postLoad at {}", ((Employee) obj).getId(), Instant.now());
		}
	}
}
