package com.intuit.model.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.NaturalId;

@Entity
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@NaturalId
	@Column(length = 70)
	private URole name;

	public Role() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public URole getName() {
		return name;
	}

	public void setName(URole name) {
		this.name = name;
	}

}
