package com.example.demo.model;

import java.util.List;
import java.util.UUID;

/**
 * Data transfer object representing user result details.
 */
public class UserResultDTO {
	private UUID userUuid;
	private List<ResultDTO> userAnswers;

	public UserResultDTO() {
	}

	public UserResultDTO(UUID userUuid, List<ResultDTO> userAnswers) {
		this.userUuid = userUuid;
		this.userAnswers = userAnswers;
	}

	public UUID getUserUuid() {
		return this.userUuid;
	}

	public void setUserUuid(UUID userUuid) {
		this.userUuid = userUuid;
	}

	public List<ResultDTO> getUserAnswers() {
		return this.userAnswers;
	}

	public void setUserAnswers(List<ResultDTO> userAnswers) {
		this.userAnswers = userAnswers;
	}

	public UserResultDTO userUuid(UUID userUuid) {
		setUserUuid(userUuid);
		return this;
	}

	public UserResultDTO userAnswers(List<ResultDTO> userQuestions) {
		setUserAnswers(userQuestions);
		return this;
	}

	@Override
	public String toString() {
		return "{" +
				" userUuid='" + getUserUuid() + "'" +
				", userQuestions='" + getUserAnswers() + "'" +
				"}";
	}

}
