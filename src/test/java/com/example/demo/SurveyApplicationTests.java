package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import com.example.demo.model.Answer;
import com.example.demo.model.Question;
import com.example.demo.model.Survey;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import net.minidev.json.JSONArray;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SurveyApplicationTests {
	@Autowired
	TestRestTemplate restTemplate;

	final String surveyBaseUri = "/api/surveys";

	final String emptyIdSurveyUri = surveyBaseUri + "/";

	final String notExistingSurveyUri = surveyBaseUri + "/99999";

	@Test
	void shouldNotReturnASurveyThatDoesNotExist() {
		ResponseEntity<Survey> response = restTemplate
				.getForEntity(notExistingSurveyUri, Survey.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	void shouldProperlyFindAnExistingSurvey() {
		ResponseEntity<String> response = restTemplate
				.getForEntity(surveyBaseUri + "/1", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody(),
				Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS));
		Number id = documentContext.read("$.id");
		String title = documentContext.read("$.title");
		String desc = documentContext.read("$.desc");

		assertThat(id).isNotNull();
		assertThat(title).isEqualTo("Example Survey 1");
		assertThat(desc).isEqualTo("Description of Example Survey 1");

		Object privateId = documentContext.read("$.privateId");
		assertThat(privateId).isNull();

	}

	@Test
	void shouldNotReturnPrivateIdOfAnExistingSurvey() {
		ResponseEntity<String> response = restTemplate
				.getForEntity(surveyBaseUri + "/1", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody(),
				Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS));

		Object privateId = documentContext.read("$.privateId");
		assertThat(privateId).isNull();
	}

	@Test
	@DirtiesContext
	void shouldNotReturnPrivateKeyOfANewSurvey() {
		String newSurveyDesc = "Here is the new description";
		String newSurveyTitle = "Super new catchy title";
		Survey newSurvey = new Survey(newSurveyTitle, newSurveyDesc);

		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity(surveyBaseUri, newSurvey, Void.class);

		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI locationOfNewSurvey = createResponse.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewSurvey, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody(),
				Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS));

		Object privateId = documentContext.read("$.privateId");
		assertThat(privateId).isNull();
	}

	@Test
	@DirtiesContext
	void shouldCreateANewSurvey() {
		String newSurveyDesc = "Here is the new description";
		String newSurveyTitle = "Super new catchy title";
		Survey newSurvey = new Survey(newSurveyTitle, newSurveyDesc);

		ResponseEntity<Void> createResponse = restTemplate
				.postForEntity(surveyBaseUri, newSurvey, Void.class);

		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI locationOfNewSurvey = createResponse.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewSurvey, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody(),
				Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS));
		Number id = documentContext.read("$.id");
		String desc = documentContext.read("$.desc");
		String title = documentContext.read("$.title");

		assertThat(id).isNotNull();
		assertThat(desc).isEqualTo(newSurveyDesc);
		assertThat(title).isEqualTo(newSurveyTitle);

		Object privateId = documentContext.read("$.privateId");
		assertThat(privateId).isNull();
	}

	@Test
	void shouldReturnAllSurveys() {
		ResponseEntity<String> response = restTemplate
				.getForEntity(surveyBaseUri, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		int questionCount = documentContext.read("$.length()");
		assertThat(questionCount).isEqualTo(3);

		JSONArray titles = documentContext.read("$..title");
		assertThat(titles).containsExactlyInAnyOrder("Example Survey 1", "Example Survey 2", "Example Survey 3");

		JSONArray descs = documentContext.read("$..desc");
		assertThat(descs).containsExactlyInAnyOrder("Description of Example Survey 1", "Description of Example Survey 2",
				"Description of Example Survey 3");
	}

	@Test
	@DirtiesContext
	void shouldDeleteAnExistingSurvey() {
		String uri = "/api/surveys/1";
		;
		ResponseEntity<Void> response = restTemplate.exchange(uri,
				HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate.getForEntity(uri, String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	void shouldNotDeleteASurveyWithNoId() {
		ResponseEntity<Void> response = restTemplate.exchange(emptyIdSurveyUri,
				HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldNotDeleteASurveyThatDoesNotExist() {
		ResponseEntity<Void> response = restTemplate.exchange(notExistingSurveyUri,
				HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	@DirtiesContext
	void shouldDeleteAllQuestionsWhenDeletingAnExistingSurvey() {
		ResponseEntity<Void> response = restTemplate.exchange("/api/surveys/1",
				HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// We check that the questions are deleted when we delete the survey
		ResponseEntity<String> getResponse = restTemplate.getForEntity("/api/surveys/1/question", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// We try to get specifically one question of the survey
		ResponseEntity<Question> getResponseToFetchQuestion = restTemplate
				.getForEntity("/api/question/1", Question.class);

		assertThat(getResponseToFetchQuestion.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	@DirtiesContext
	void shouldDeleteAllAnswersWhenDeletingAnExistingSurvey() {
		ResponseEntity<Void> response = restTemplate.exchange("/api/surveys/1",
				HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// We check that the questions are deleted when we delete the survey
		ResponseEntity<String> getResponse = restTemplate.getForEntity("/api/surveys/1/answer", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// We try to get specifically one question of the survey
		ResponseEntity<Answer> getResponseToFetchAnswer = restTemplate
				.getForEntity("/api/answer/1", Answer.class);

		assertThat(getResponseToFetchAnswer.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

}
