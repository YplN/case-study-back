package com.example.demo.model;

import java.util.List;
import java.util.UUID;

/**
 * Data class representing a User submission.
 */
public class UserSubmission {
	private UUID userUuid;
	private List<Submission> submissions;

	public UserSubmission() {
	}

	public UserSubmission(UUID userUuid, List<Submission> submissions) {
		this.userUuid = userUuid;
		this.submissions = submissions;
	}

	public UUID getUserUuid() {
		return this.userUuid;
	}

	public void setUserUuid(UUID userUuid) {
		this.userUuid = userUuid;
	}

	public List<Submission> getSubmissions() {
		return this.submissions;
	}

	public void setSubmissions(List<Submission> submissions) {
		this.submissions = submissions;
	}

	public UserSubmission userUuid(UUID userUuid) {
		setUserUuid(userUuid);
		return this;
	}

	public UserSubmission submissions(List<Submission> submissions) {
		setSubmissions(submissions);
		return this;
	}

	@Override
	public String toString() {
		return "{" +
				" userUuid='" + getUserUuid() + "'" +
				", submissions='" + getSubmissions() + "'" +
				"}";
	}

}
