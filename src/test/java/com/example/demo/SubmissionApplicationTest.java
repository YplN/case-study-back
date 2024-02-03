package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import com.example.demo.model.Question;
import com.example.demo.model.Submission;
import com.example.demo.model.Survey;
import com.example.demo.model.UserSubmission;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SubmissionApplicationTest {

	@Autowired
	TestRestTemplate restTemplate;

	final String uuidUser1 = "00000000-0000-0000-0000-000000000001";
	final String uuidUser2 = "00000000-0000-0000-0000-000000000002";
	final String uuidUser3 = "00000000-0000-0000-0000-000000000003";

	@Test
	@DirtiesContext
	void shouldProcessANewUserSubmission() {
		ArrayList<Submission> newSubmission = new ArrayList<Submission>();
		Submission newSubmission1 = new Submission(1L, 1);
		Submission newSubmission2 = new Submission(2L, 2);
		Submission newSubmission3 = new Submission(3L, 3);
		newSubmission.addAll(Arrays.asList(newSubmission1, newSubmission2, newSubmission3));

		UUID uuidUser4 = UUID.fromString("00000000-0000-0000-0000-000000000004");

		UserSubmission newUserSubmission = new UserSubmission(uuidUser4,
				newSubmission);

		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity("/api/surveys/1/submit", newUserSubmission, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		ResponseEntity<String> getResponse = restTemplate.getForEntity("/api/surveys/1/results", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		JSONArray userUuids = documentContext.read("$[*].userUuid");
		assertThat(userUuids).contains(uuidUser4.toString());

		JSONArray ratings = documentContext.read("$[*].userAnswers[*].answerRating");
		assertThat(ratings).containsSequence(1, 2, 3);
	}

	@Test
	void shouldNotProcessAUserSubmissionIfTheUserAlreadyReplied() {
		ArrayList<Submission> newSubmission = new ArrayList<Submission>();
		Submission newSubmission1 = new Submission(1L, 1);
		Submission newSubmission2 = new Submission(2L, 2);
		Submission newSubmission3 = new Submission(3L, 3);
		newSubmission.addAll(Arrays.asList(newSubmission1, newSubmission2, newSubmission3));

		UserSubmission newUserSubmission = new UserSubmission(UUID.fromString(uuidUser1), newSubmission);

		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity("/api/surveys/1/submit", newUserSubmission, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void shouldNotProcessAUserSubmissionIfOneOfTheQuestionsDoNotBelongToTheSurvey() {
		ArrayList<Submission> newSubmission = new ArrayList<Submission>();
		Submission newSubmission1 = new Submission(10L, 1); // The question with ID 10 is not in survey 1
		Submission newSubmission2 = new Submission(2L, 2);
		Submission newSubmission3 = new Submission(3L, 3);
		newSubmission.addAll(Arrays.asList(newSubmission1, newSubmission2, newSubmission3));

		UserSubmission newUserSubmission = new UserSubmission(UUID.fromString(uuidUser1), newSubmission);

		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity("/api/surveys/1/submit", newUserSubmission, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	@DirtiesContext
	void shouldProcessANewUserSubmissionEvenIfAllQuestionsAreNotAnswered() {
		ArrayList<Submission> newSubmission = new ArrayList<Submission>();
		Submission newSubmission1 = new Submission(1L, 1);
		Submission newSubmission2 = new Submission(2L, 2);
		// question 3 is not answered
		newSubmission.addAll(Arrays.asList(newSubmission1, newSubmission2));

		UUID uuidUser4 = UUID.fromString("00000000-0000-0000-0000-000000000004");

		UserSubmission newUserSubmission = new UserSubmission(uuidUser4,
				newSubmission);

		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity("/api/surveys/1/submit", newUserSubmission, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	@DirtiesContext
	void shouldProcessANewUserSubmissionEvenIfOneRatingIsNull() {
		ArrayList<Submission> newSubmission = new ArrayList<Submission>();
		Submission newSubmission1 = new Submission(1L, 1);
		Submission newSubmission2 = new Submission(2L, 2);
		Submission newSubmission3 = new Submission(3L, null);
		newSubmission.addAll(Arrays.asList(newSubmission1, newSubmission2, newSubmission3));

		UUID uuidUser4 = UUID.fromString("00000000-0000-0000-0000-000000000004");

		UserSubmission newUserSubmission = new UserSubmission(uuidUser4,
				newSubmission);

		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity("/api/surveys/1/submit", newUserSubmission, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	@DirtiesContext
	void shouldReturnTheListOfSurveysAnsweredWithTheSameUserUuid() {

		// We create a new submission for a new user that will be answering only survey
		// 1
		ArrayList<Submission> newSubmission = new ArrayList<Submission>();
		Submission newSubmission1 = new Submission(1L, 1);
		Submission newSubmission2 = new Submission(2L, 2);
		Submission newSubmission3 = new Submission(3L, 3);
		newSubmission.addAll(Arrays.asList(newSubmission1, newSubmission2, newSubmission3));

		UUID uuidUser4 = UUID.fromString("00000000-0000-0000-0000-000000000004");

		UserSubmission newUserSubmission = new UserSubmission(uuidUser4,
				newSubmission);

		// We create the submission
		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity("/api/surveys/1/submit", newUserSubmission, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		// We get the list of the surveys answered and not answered by User4
		ResponseEntity<Object> getResponse = restTemplate
				.getForEntity("/api/surveys/user/" + uuidUser4.toString(), Object.class);

		Object body = getResponse.getBody();
		assertThat(body.getClass()).isEqualTo(LinkedHashMap.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		// We convert the object into the appropriate type
		ObjectMapper objectMapper = new ObjectMapper();
		LinkedHashMap<String, List<Survey>> responseObject = objectMapper.convertValue(body,
				new TypeReference<LinkedHashMap<String, List<Survey>>>() {
				});

		List<Survey> answered = responseObject.get("answered");
		List<Survey> notAnswered = responseObject.get("notAnswered");

		assertThat(answered.size()).isEqualTo(1);
		assertThat(notAnswered.size()).isEqualTo(2);

		// Answered survey
		assertThat(answered.get(0).getId()).isEqualTo(1);

		// notAnswered survey
		assertThat(notAnswered.get(0).getId()).isEqualTo(2);
		assertThat(notAnswered.get(1).getId()).isEqualTo(3);
	}

	@Test
	void shouldReturnTheSurveysNotAnsweredByUserUuid() {
		String uuidUser4 = "00000000-0000-0000-0000-000000000004";
		// We check if the user 4 answered the survey 1
		ResponseEntity<Object> getResponse = restTemplate
				.getForEntity("/api/surveys/1/user/" + uuidUser4, Object.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		Object body = getResponse.getBody();

		// We convert the object into the appropriate type
		ObjectMapper objectMapper = new ObjectMapper();
		ArrayList<Question> questions = objectMapper.convertValue(body,
				new TypeReference<ArrayList<Question>>() {
				});

		assertThat(questions.size()).isEqualTo(3);
		assertThat(questions.get(0).getSurvey().getId()).isEqualTo(1);
		assertThat(questions.get(0).getId()).isEqualTo(1);
		assertThat(questions.get(1).getSurvey().getId()).isEqualTo(1);
		assertThat(questions.get(1).getId()).isEqualTo(2);
		assertThat(questions.get(2).getSurvey().getId()).isEqualTo(1);
		assertThat(questions.get(2).getId()).isEqualTo(3);
	}

	@Test
	void shouldNotReturnTheSurveysUserUuidIfSurveyIdDoesNotExist() {
		// We check if the user 1 answered the survey 9999
		ResponseEntity<Object> getResponse = restTemplate
				.getForEntity("/api/surveys/9999/user/" + uuidUser1, Object.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

}
