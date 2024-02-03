package com.example.demo.model;

/**
 * Data transfer object representing survey result details.
 */
public class ResultDTO {
	private Long idQuestion;
	private Long idAnswer;
	private String textQuestion;
	private Integer answerRating;

	public ResultDTO() {
	}

	public ResultDTO(Long idQuestion, Long idAnswer, String textQuestion, Integer answerRating) {
		this.idQuestion = idQuestion;
		this.idAnswer = idAnswer;
		this.textQuestion = textQuestion;
		this.answerRating = answerRating;
	}

	public Long getIdQuestion() {
		return this.idQuestion;
	}

	public void setIdQuestion(Long idQuestion) {
		this.idQuestion = idQuestion;
	}

	public Long getIdAnswer() {
		return this.idAnswer;
	}

	public void setIdAnswer(Long idAnswer) {
		this.idAnswer = idAnswer;
	}

	public String getTextQuestion() {
		return this.textQuestion;
	}

	public void setTextQuestion(String textQuestion) {
		this.textQuestion = textQuestion;
	}

	public Integer getAnswerRating() {
		return this.answerRating;
	}

	public void setAnswerRating(Integer answerRating) {
		this.answerRating = answerRating;
	}

	public ResultDTO idQuestion(Long idQuestion) {
		setIdQuestion(idQuestion);
		return this;
	}

	public ResultDTO idAnswer(Long idAnswer) {
		setIdAnswer(idAnswer);
		return this;
	}

	public ResultDTO textQuestion(String textQuestion) {
		setTextQuestion(textQuestion);
		return this;
	}

	public ResultDTO answerRating(Integer answerRating) {
		setAnswerRating(answerRating);
		return this;
	}

	@Override
	public String toString() {
		return "{" +
				" idQuestion='" + getIdQuestion() + "'" +
				", idAnswer='" + getIdAnswer() + "'" +
				", textQuestion='" + getTextQuestion() + "'" +
				", answerRating='" + getAnswerRating() + "'" +
				"}";
	}

}
