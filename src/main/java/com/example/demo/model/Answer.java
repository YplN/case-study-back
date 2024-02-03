package com.example.demo.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * Entity class representing an Answer.
 */
@Entity
@Table(name = "ANSWER")
public class Answer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "answer_id")
	private Long id;

	@Column(name = "answer_rating")
	private Integer rating;

	@Column(name = "user_uuid", nullable = false)
	private UUID userUuid;

	@ManyToOne
	@JoinColumn(name = "question_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Question question;

	final static Integer MIN_VALUE = 1;
	final static Integer MAX_VALUE = 5;

	public Answer() {
	}

	public Answer(Long id, Integer rating) {
		this.id = id;
		this.rating = validateRating(rating);
	}

	public Answer(Long id, Integer rating, Question question) {
		this.id = id;
		this.question = question;
		this.rating = validateRating(rating);
	}

	public Answer(Long id, Integer rating, Question question, UUID userUuid) {
		this.id = id;
		this.question = question;
		this.userUuid = userUuid;
		this.rating = validateRating(rating);
	}

	public Answer(Integer rating, Question question) {
		this.question = question;
		this.rating = validateRating(rating);
	}

	public Answer(Integer rating) {
		this.rating = validateRating(rating);
	}

	public Long getId() {
		return id;
	}

	public UUID getUserUuid() {
		return userUuid;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUserUuId(UUID userUuid) {
		this.userUuid = userUuid;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public Question getQuestion() {
		return question;
	}

	@Override
	public String toString() {
		return "{" +
				" id='" + getId() + "'" +
				", rating='" + getRating() + "'" +
				", question='" + getQuestion() + "'" +
				", userUuid='" + getUserUuid() + "'" +
				"}";
	}

	/**
	 * Validates the rating value to ensure it falls within the acceptable range.
	 * 
	 * @param rating The rating value to validate.
	 * @return The validated rating value.
	 */
	private Integer validateRating(Integer rating) {
		if (rating != null) {
			return Math.min(MAX_VALUE, Math.max(MIN_VALUE, rating));
		} else {
			return null;
		}
	}

}
