package com.example.demo.model;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * Entity class representing a Survey.
 */
@Entity
@Table(name = "SURVEY")
public class Survey {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "survey_id")
	private Long id;

	@Column(name = "survey_uuid")
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID uuid;

	@Column(name = "survey_title")
	private String title;
	@Column(name = "survey_desc")
	private String desc;

	protected Survey() {
	}

	public Survey(String title, String desc) {
		this.title = title;
		this.desc = desc;
		this.uuid = java.util.UUID.randomUUID();
	}

	public Long getId() {
		return id;
	}

	private UUID getUuid() {
		return uuid;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	@Override
	public String toString() {
		return "SURVEY \n\tid=" + id.toString() + ",\n\ttitle=" + title + "\n\tdesc="
				+ desc + "\n\t"
				+ "\n---------";
	}

}