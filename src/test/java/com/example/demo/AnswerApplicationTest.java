
package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.UUID;

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

import com.example.demo.model.Answer;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AnswerApplicationTest {
	@Autowired
	TestRestTemplate restTemplate;

	final String answerBaseUri = "/api/answer";
	final String emptyIAnswerUri = answerBaseUri + "/";
	final String notExistingAnswerUri = answerBaseUri + "/99999";
	final String existingAnswerUri = answerBaseUri + "/1";
	final String uuidUser1 = "00000000-0000-0000-0000-000000000001";
	final String uuidUser2 = "00000000-0000-0000-0000-000000000002";
	final String uuidUser3 = "00000000-0000-0000-0000-000000000003";

	@Test
	void shouldProperlyFindAnExistingAnswer() {
		ResponseEntity<String> response = restTemplate
				.getForEntity(existingAnswerUri, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());

		Number id = documentContext.read("$.id");
		Number rating = documentContext.read("$.rating");
		String userUuid = documentContext.read("$.userUuid");

		assertThat(id).isNotNull();
		assertThat(rating).isEqualTo(4);
		assertThat(userUuid).isEqualTo(uuidUser1);

	}

	@Test
	void shouldAcceptToCreateAnAnswerWithNullAsRating() {
		Answer newAnswer = new Answer(null);
		UUID newUuid = UUID.randomUUID();
		newAnswer.setUserUuId(newUuid);

		String url = "/api/question/1/answer";
		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity(url, newAnswer, Void.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	void shouldNotReturnAnAnswerThatDoesNotExist() {
		ResponseEntity<Answer> response = restTemplate
				.getForEntity(notExistingAnswerUri, Answer.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	@DirtiesContext
	void shouldCreateANewAnswer() {
		Integer newAnswerRating = 4;
		UUID newUuid = UUID.randomUUID();
		Answer newAnswer = new Answer(newAnswerRating);
		newAnswer.setUserUuId(newUuid);
		String url = "/api/question/1/answer";

		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity(url, newAnswer, Void.class);

		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI locationOfNewAnswer = createResponse.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewAnswer, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		Number text = documentContext.read("$.rating");
		Number questionId = documentContext.read("$.question.id");
		String userUuid = documentContext.read("$.userUuid");

		assertThat(id).isNotNull();
		assertThat(text).isEqualTo(newAnswerRating);
		assertThat(userUuid).isEqualTo(newUuid.toString());
		assertThat(questionId).isEqualTo(1); // Verify the associated question ID
	}

	@Test
	void shouldReturnAllAnswers() {
		ResponseEntity<String> response = restTemplate
				.getForEntity(answerBaseUri, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		int questionCount = documentContext.read("$.length()");
		assertThat(questionCount).isEqualTo(23);

		// We want just the top level ids (ignore the ids in the children, that is for
		// example the id corresponding to the questions and survey fields)
		JSONArray ids = documentContext.read("$[*].id");
		assertThat(ids).containsExactlyInAnyOrder(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21,
				22, 23);

		JSONArray ratings = documentContext.read("$..rating");
		assertThat(ratings).containsSequence(4, 4, 2, 5, 4, 5, 1, 4, 4, 1, 4, 3, 2, 5, 4, 3, 5, 1, 4, 2, 3, 5, 5);

		JSONArray userUuids = documentContext.read("$..userUuid");
		assertThat(userUuids).containsOnly(uuidUser1, uuidUser2, uuidUser3);

	}

	@Test
	void shouldNotUpdateAnAnswerThatDoesNotExist() {
		Answer unknownAnswer = new Answer(1);
		HttpEntity<Answer> request = new HttpEntity<>(unknownAnswer);
		ResponseEntity<Void> response = restTemplate.exchange(notExistingAnswerUri,
				HttpMethod.PUT, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	void shouldNotCreateAnAnswerWithNoUserUuid() {
		Answer unknownAnswer = new Answer(1);
		String url = "/api/question/1/answer";
		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity(url, unknownAnswer, Void.class);

		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	@DirtiesContext
	void shouldNotUpdateAnExistingAnswerIfItIsNotTheSameUserUuid() {
		Integer newRating = 1;
		Answer newAnswer = new Answer(newRating);

		// We set the newAnswer with the uuid of the user2
		newAnswer.setUserUuId(UUID.fromString(uuidUser2));

		HttpEntity<Answer> request = new HttpEntity<>(newAnswer);
		ResponseEntity<Void> response = restTemplate.exchange(existingAnswerUri,
				HttpMethod.PUT, request, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

		ResponseEntity<String> getResponse = restTemplate.getForEntity(existingAnswerUri, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		Number rating = documentContext.read("$.rating");
		assertThat(id).isEqualTo(1);
		// We check that the rating was not changed
		assertThat(rating).isEqualTo(4);

	}

	@Test
	@DirtiesContext
	void shouldUpdateAnExistingAnswer() {
		Integer newRating = 1;
		Answer newAnswer = new Answer(newRating);
		// We set the newAnswer with the uuid of the user1
		newAnswer.setUserUuId(UUID.fromString(uuidUser1));

		HttpEntity<Answer> request = new HttpEntity<>(newAnswer);
		ResponseEntity<Void> response = restTemplate.exchange(existingAnswerUri,
				HttpMethod.PUT, request, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate.getForEntity(existingAnswerUri, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		Number rating = documentContext.read("$.rating");
		assertThat(id).isEqualTo(1);
		assertThat(rating).isEqualTo(newRating);

	}

	@Test
	@DirtiesContext
	void shouldNotChangeNumberOfAnswersWhenUpdatingAnExistingAnswer() {

		// We get the number of answers in an existing survey
		ResponseEntity<String> response = restTemplate
				.getForEntity("/api/surveys/1/answer", String.class);
		DocumentContext documentContext = JsonPath.parse(response.getBody());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		int oldCount = documentContext.read("$.length()");

		// We create a new Answer with the uuid of the user1 and update it in the
		// database
		Answer newAnswer = new Answer(1);
		newAnswer.setUserUuId(UUID.fromString(uuidUser1));
		HttpEntity<Answer> request = new HttpEntity<>(newAnswer);
		ResponseEntity<Void> updateResponse = restTemplate.exchange(existingAnswerUri,
				HttpMethod.PUT, request, Void.class);
		assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// We get the number of answers in the previous survey and check it is the same
		// than before
		ResponseEntity<String> newGetResponse = restTemplate
				.getForEntity("/api/surveys/1/answer", String.class);
		DocumentContext newDocumentContext = JsonPath.parse(newGetResponse.getBody());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		int newCount = newDocumentContext.read("$.length()");
		assertThat(newCount).isEqualTo(oldCount);
	}

	@Test
	@DirtiesContext
	void shouldDeleteAnExistingAnswer() {
		ResponseEntity<Void> response = restTemplate.exchange(existingAnswerUri,
				HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate.getForEntity(existingAnswerUri, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	void shouldNotDeleteAnAnswerThatDoesNotExist() {
		ResponseEntity<Void> response = restTemplate.exchange(notExistingAnswerUri,
				HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	void shouldReturnAllAnswersOfAnExistingSurvey() {
		ResponseEntity<String> response = restTemplate
				.getForEntity("/api/surveys/1/results", String.class);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// We check the number of users that answered the survey
		int numberOfUsers = documentContext.read("$.length()");
		assertThat(numberOfUsers).isEqualTo(3);

		// User 1 ----------------------------------------------------
		// We check the id of their answers
		JSONArray firstUserIdAnswers = documentContext.read("$[0].userAnswers[*].idAnswer");
		assertThat(firstUserIdAnswers).containsExactlyInAnyOrder(1, 2);
		// We check the id of the question answered
		JSONArray firstUserIdQuestionsAnswered = documentContext.read("$[0].userAnswers[*].idQuestion");
		assertThat(firstUserIdQuestionsAnswered).containsExactlyInAnyOrder(1, 3);
		// We check their ratings
		JSONArray firstUserIdRatings = documentContext.read("$[0].userAnswers[*].answerRating");
		assertThat(firstUserIdRatings).containsExactlyInAnyOrder(4, 4);

		// We also check for this user that the ratings correspond to the correct
		// questions
		int firstUserNumberOfAnswers = documentContext.read("$[0].userAnswers.length()");
		assertThat(firstUserNumberOfAnswers).isEqualTo(2);

		// First answer
		Number firstUserFirstAnswerId = documentContext.read("$[0].userAnswers[0].idAnswer");
		Number firstUserFirstAnswerQuestionId = documentContext.read("$[0].userAnswers[0].idQuestion");
		Number firstUserFirstAnswerRating = documentContext.read("$[0].userAnswers[0].answerRating");

		assertThat(firstUserFirstAnswerId).isEqualTo(1);
		assertThat(firstUserFirstAnswerQuestionId).isEqualTo(1);
		assertThat(firstUserFirstAnswerRating).isEqualTo(4);

		// Second answer
		Number firstUserSecondAnswerId = documentContext.read("$[0].userAnswers[1].idAnswer");
		Number firstUserSecondAnswerQuestionId = documentContext.read("$[0].userAnswers[1].idQuestion");
		Number firstUserSecondAnswerRating = documentContext.read("$[0].userAnswers[1].answerRating");

		assertThat(firstUserSecondAnswerId).isEqualTo(2);
		assertThat(firstUserSecondAnswerQuestionId).isEqualTo(3);
		assertThat(firstUserSecondAnswerRating).isEqualTo(4);

		// User 2 ----------------------------------------------------
		// We check the id of their answers
		JSONArray secondUserIdAnswers = documentContext.read("$[1].userAnswers[*].idAnswer");
		assertThat(secondUserIdAnswers).containsExactlyInAnyOrder(9, 10);
		// We check the id of the question answered
		JSONArray secondUserIdQuestionsAnswered = documentContext.read("$[1].userAnswers[*].idQuestion");
		assertThat(secondUserIdQuestionsAnswered).containsExactlyInAnyOrder(1, 3);
		// We check their ratings
		JSONArray secondUserIdRatings = documentContext.read("$[1].userAnswers[*].answerRating");
		assertThat(secondUserIdRatings).containsExactlyInAnyOrder(4, 1);

		// User 2 ----------------------------------------------------
		// We check the id of their answers
		JSONArray thirdUserIdAnswers = documentContext.read("$[2].userAnswers[*].idAnswer");
		assertThat(thirdUserIdAnswers).containsExactlyInAnyOrder(16);
		// We check the id of the question answered
		JSONArray thirdUserIdQuestionsAnswered = documentContext.read("$[2].userAnswers[*].idQuestion");
		assertThat(thirdUserIdQuestionsAnswered).containsExactlyInAnyOrder(3);
		// We check their ratings
		JSONArray thirdUserIdRatings = documentContext.read("$[2].userAnswers[*].answerRating");
		assertThat(thirdUserIdRatings).containsExactlyInAnyOrder(3);

	}

	@Test
	void shouldNotReturnAnswersOfAQuestionThatDoesNotExist() {
		ResponseEntity<String> response = restTemplate
				.getForEntity("/api/question/999999/answer", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	@DirtiesContext
	void shouldDeleteAllAnswersOfAnExistingQuestion() {
		ResponseEntity<Void> response = restTemplate.exchange("/api/question/1/answer",
				HttpMethod.DELETE, null, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate.getForEntity("/api/question/1/answer", String.class);

		// We should return OK because the answer list is now set to []
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		int questionCount = documentContext.read("$.length()");

		assertThat(questionCount).isEqualTo(0);

	}

}