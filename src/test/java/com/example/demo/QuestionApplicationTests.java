package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import com.example.demo.model.Question;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuestionApplicationTests {
	@Autowired
	TestRestTemplate restTemplate;

	final String questionBaseUri = "/api/question";
	final String emptyIdQuestionUri = questionBaseUri + "/";
	final String notExistingQuestionUri = questionBaseUri + "/99999";

	@Test
	void shouldProperlyFindAnExistingQuestion() {
		ResponseEntity<String> response = restTemplate
				.getForEntity(questionBaseUri + "/1", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		Number id = documentContext.read("$.id");
		String text = documentContext.read("$.text");

		assertThat(id).isNotNull();
		assertThat(text).isEqualTo("Question 1.1");

	}

	// @Test
	// void shouldNotReturnAQuestionWithEmptyId() {
	// ResponseEntity<String> response = restTemplate
	// .getForEntity(emptyIdQuestionUri, String.class);

	// assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	// }

	@Test
	void shouldNotReturnAQuestionThatDoesNotExist() {
		ResponseEntity<Question> response = restTemplate
				.getForEntity(notExistingQuestionUri, Question.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	@DirtiesContext
	void shouldCreateANewQuestion() {
		String newQuestionText = "Is this a new question?";
		Question newQuestion = new Question(newQuestionText);
		String url = "/api/surveys/1/question";

		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity(url, newQuestion, Void.class);

		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI locationOfNewQuestion = createResponse.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewQuestion, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		String text = documentContext.read("$.text");
		Number surveyId = documentContext.read("$.survey.id");

		assertThat(id).isNotNull();
		assertThat(text).isEqualTo(newQuestionText);
		assertThat(surveyId).isEqualTo(1); // Verify the associated survey ID
	}

	@Test
	void shouldRejectToCreateAQuestionWithEmptyText() {
		Question newQuestion = new Question();
		String url = "/api/surveys/1/question";
		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity(url, newQuestion, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void shouldReturnAllQuestions() {
		ResponseEntity<String> response = restTemplate
				.getForEntity(questionBaseUri, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		int questionCount = documentContext.read("$.length()");
		assertThat(questionCount).isEqualTo(10);

		// We want just the top level ids (ignore the ids in the children, that is for
		// example the id corresponding to the survey)
		JSONArray ids = documentContext.read("$[*].id");
		assertThat(ids).containsExactlyInAnyOrder(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

		JSONArray texts = documentContext.read("$..text");
		assertThat(texts).containsExactlyInAnyOrder("Question 1.1", "Question 1.2", "Question 1.3", "Question 2.1",
				"Question 2.2", "Question 2.3", "Question 2.4", "Question 2.5", "Question 3.1", "Question 3.2");
	}

	@Test
	void shouldNotUpdateAQuestionThatDoesNotExist() {
		Question unknownQuestion = new Question("unknown");
		HttpEntity<Question> request = new HttpEntity<>(unknownQuestion);
		ResponseEntity<Void> response = restTemplate.exchange(notExistingQuestionUri,
				HttpMethod.PUT, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	void shouldNotUpdateAQuestionWithNoId() {
		Question unknownQuestion = new Question("unknown");
		HttpEntity<Question> request = new HttpEntity<>(unknownQuestion);
		ResponseEntity<Void> response = restTemplate.exchange(emptyIdQuestionUri,
				HttpMethod.PUT, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	@DirtiesContext
	void shouldUpdateAnExistingQuestion() {
		String uri = "/api/question/1";
		String newText = "Is my question updated?";
		Question newQuestion = new Question(newText);
		HttpEntity<Question> request = new HttpEntity<>(newQuestion);
		ResponseEntity<Void> response = restTemplate.exchange(uri,
				HttpMethod.PUT, request, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate.getForEntity(uri, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		String text = documentContext.read("$.text");
		assertThat(id).isEqualTo(1);
		assertThat(text).isEqualTo(newText);

	}

	@Test
	@DirtiesContext
	void shouldDeleteAnExistingQuestion() {
		String uri = "/api/question/1";
		;
		ResponseEntity<Void> response = restTemplate.exchange(uri,
				HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate.getForEntity(uri, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	void shouldNotDeleteAQuestionWithNoId() {
		ResponseEntity<Void> response = restTemplate.exchange(emptyIdQuestionUri,
				HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldNotDeleteAQuestionThatDoesNotExist() {
		ResponseEntity<Void> response = restTemplate.exchange(notExistingQuestionUri,
				HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	void shouldReturnAllQuestionsOfAnExistingSurvey() {
		ResponseEntity<String> response = restTemplate
				.getForEntity("/api/surveys/1/question", String.class);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		int questionCount = documentContext.read("$.length()");
		assertThat(questionCount).isEqualTo(3);

		// We want just the top level ids (ignore the ids in the children, that is for
		// example the id corresponding to the survey)
		JSONArray ids = documentContext.read("$[*].id");
		assertThat(ids).containsExactlyInAnyOrder(1, 2, 3);

		JSONArray texts = documentContext.read("$..text");
		assertThat(texts).containsExactlyInAnyOrder("Question 1.1", "Question 1.2", "Question 1.3");

		// We check that the surveys containing the questions are the correct ones
		JSONArray surveyIds = documentContext.read("$[*].survey.id");
		assertThat(surveyIds).containsOnly(1);
	}

	@Test
	void shouldNotReturnQuestionsOfASurveyThatDoesNotExist() {
		ResponseEntity<String> response = restTemplate
				.getForEntity("/api/surveys/999999/question", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	void shouldReturnNotFoundWhenDeletingQuestionsOfASurveyThatDoesNotExist() {
		ResponseEntity<Void> response = restTemplate
				.exchange("/api/surveys/999999/question", HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	@DirtiesContext
	void shouldDeleteAllQuestionsOfAnExistingSurvey() {
		ResponseEntity<Void> response = restTemplate.exchange("/api/surveys/1/question",
				HttpMethod.DELETE, null, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate.getForEntity("/api/surveys/1/question", String.class);

		// We should return OK because the question list is now set to []
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		int questionCount = documentContext.read("$.length()");

		assertThat(questionCount).isEqualTo(0);

	}

}