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

/**
 * Entity class representing a Question.
 */
@Entity
@Table(name = "QUESTION")
public class Question {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "question_id")
	private Long id;

	@Column(name = "question_text")
	private String text;

	@ManyToOne
	@JoinColumn(name = "survey_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Survey survey;

	public Question() {
	}

	public Question(Long id, String text) {
		this.id = id;
		this.text = text;
	}

	public Question(Long id, String text, Survey survey) {
		this.id = id;
		this.text = text;
		this.survey = survey;
	}

	public Question(String text, Survey survey) {
		this.text = text;
		this.survey = survey;
	}

	public Question(String text) {
		this.text = text;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public Survey getSurvey() {
		return survey;
	}

}
