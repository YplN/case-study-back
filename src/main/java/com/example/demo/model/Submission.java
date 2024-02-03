package com.example.demo.model;

/**
 * Data class representing a submission.
 */
public class Submission {
	private Long questionId;
	private Integer rating;

	public Submission() {
	}

	public Submission(Long questionId, Integer rating) {
		this.questionId = questionId;
		this.rating = rating;
	}

	public Long getQuestionId() {
		return this.questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Integer getRating() {
		return this.rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Submission questionId(Long questionId) {
		setQuestionId(questionId);
		return this;
	}

	public Submission rating(Integer rating) {
		setRating(rating);
		return this;
	}

	@Override
	public String toString() {
		return "{" +
				" questionId='" + getQuestionId() + "'" +
				", rating='" + getRating() + "'" +
				"}";
	}

}
