package com.example.demo.model;

import java.util.List;

public class SurveyResultDTO {
	private Long id;
	private String title;
	private String desc;
	private List<QuestionDTO> questions;

	public SurveyResultDTO() {
	}

	public SurveyResultDTO(Long id, String title, String desc, List<QuestionDTO> questions) {
		this.id = id;
		this.title = title;
		this.desc = desc;
		this.questions = questions;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return this.desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<QuestionDTO> getQuestions() {
		return this.questions;
	}

	public void setQuestions(List<QuestionDTO> questions) {
		this.questions = questions;
	}

	public SurveyResultDTO id(Long id) {
		setId(id);
		return this;
	}

	public SurveyResultDTO title(String title) {
		setTitle(title);
		return this;
	}

	public SurveyResultDTO desc(String desc) {
		setDesc(desc);
		return this;
	}

	public SurveyResultDTO questions(List<QuestionDTO> questions) {
		setQuestions(questions);
		return this;
	}

	@Override
	public String toString() {
		return "{" +
				" id='" + getId() + "'" +
				", title='" + getTitle() + "'" +
				", desc='" + getDesc() + "'" +
				", questions='" + getQuestions() + "'" +
				"}";
	}

}