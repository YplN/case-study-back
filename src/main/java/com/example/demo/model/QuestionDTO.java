package com.example.demo.model;

import java.util.List;

/**
 * Data transfer object representing question result details.
 */
public class QuestionDTO {
	private Long id;
	private String text;
	private List<AnswerDTO> answers;

	public QuestionDTO() {
	}

	public QuestionDTO(Long id, String text, List<AnswerDTO> answers) {
		this.id = id;
		this.text = text;
		this.answers = answers;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<AnswerDTO> getAnswers() {
		return this.answers;
	}

	public void setAnswers(List<AnswerDTO> answers) {
		this.answers = answers;
	}

	public QuestionDTO id(Long id) {
		setId(id);
		return this;
	}

	public QuestionDTO text(String text) {
		setText(text);
		return this;
	}

	public QuestionDTO answers(List<AnswerDTO> answers) {
		setAnswers(answers);
		return this;
	}

	@Override
	public String toString() {
		return "{" +
				" id='" + getId() + "'" +
				", text='" + getText() + "'" +
				", answers='" + getAnswers() + "'" +
				"}";
	}

}