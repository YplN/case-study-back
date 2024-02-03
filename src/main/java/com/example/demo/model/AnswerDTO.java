package com.example.demo.model;

import java.util.UUID;

public class AnswerDTO {
	private Long id;
	private int rating;
	private UUID userUuid;

	public AnswerDTO() {
	}

	public AnswerDTO(Long id, int rating) {
		this.id = id;
		this.rating = rating;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUuid(UUID uuid) {
		this.userUuid = uuid;
	}

	public UUID getUserUuid() {
		return userUuid;
	}

	public int getRating() {
		return this.rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public AnswerDTO id(Long id) {
		setId(id);
		return this;
	}

	public AnswerDTO rating(int rating) {
		setRating(rating);
		return this;
	}

	@Override
	public String toString() {
		return "{" +
				" id='" + getId() + "'" +
				", rating='" + getRating() + "'" +
				"}";
	}

}