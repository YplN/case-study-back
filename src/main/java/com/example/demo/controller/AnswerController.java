package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Answer;
import com.example.demo.model.Question;
import com.example.demo.model.SurveyResultDTO;
import com.example.demo.model.UserResultDTO;
import com.example.demo.model.UserSubmission;
import com.example.demo.service.AnswerService;
import com.example.demo.service.QuestionService;
import com.example.demo.service.ResponseService;

import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller class for handling answer-related endpoints.
 */
@RestController
@RequestMapping("/api")
public class AnswerController {

	private final AnswerService answerService;
	private final QuestionService questionService;
	private final ResponseService responseService;

	/**
	 * Constructor for AnswerController.
	 * 
	 * @param answerService   Service for managing answers.
	 * @param questionService Service for managing questions.
	 * @param surveyService   Service for managing surveys.
	 * @param responseService Service for managing responses.
	 */
	public AnswerController(AnswerService answerService, QuestionService questionService,
			ResponseService responseService) {
		this.answerService = answerService;
		this.questionService = questionService;
		this.responseService = responseService;
	}

	/**
	 * Retrieves all answers.
	 * 
	 * @return ResponseEntity containing all answers if found, otherwise returns no
	 *         content.
	 */
	@GetMapping("/answer")
	public ResponseEntity<Iterable<Answer>> getAllAnswers() {
		Iterable<Answer> answers = answerService.getAllAnswers();
		if (answers == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(answers);
	}

	/**
	 * Retrieves all answers for a specific question.
	 * 
	 * @param questionId The ID of the question.
	 * @return ResponseEntity containing answers for the question if found,
	 *         otherwise returns no content.
	 */
	@GetMapping("/question/{questionId}/answer")
	public ResponseEntity<Iterable<Answer>> getAllAnswersForQuestion(@PathVariable("questionId") Long questionId) {

		Question question = questionService.findQuestion(questionId);

		if (question == null) {
			return ResponseEntity.noContent().build();
		}

		Iterable<Answer> answers = answerService.findByQuestionId(questionId);
		if (answers == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(answers);
	}

	/**
	 * Retrieves an answer by its ID.
	 * 
	 * @param answerId The ID of the answer.
	 * @return ResponseEntity containing the answer if found, otherwise returns no
	 *         content.
	 */
	@GetMapping("/answer/{answerId}")
	public ResponseEntity<Answer> getAnswerById(@PathVariable("answerId") Long answerId) {
		Answer answer = answerService.getAnswerById(answerId).orElse(null);

		if (answer == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(answer);
	}

	/**
	 * Creates a new answer for a question.
	 * 
	 * @param questionId The ID of the question.
	 * @param newAnswer  The new answer to be created.
	 * @param ucb        UriComponentsBuilder for building URI.
	 * @return ResponseEntity with the URI of the newly created answer.
	 */
	@PostMapping("/question/{questionId}/answer")
	public ResponseEntity<Void> createAnswer(@PathVariable("questionId") Long questionId, @RequestBody Answer newAnswer,
			UriComponentsBuilder ucb) {

		// We reject the request if the answer does not have a user uuid present
		if (newAnswer.getUserUuid() == null) {
			return ResponseEntity.badRequest().build();
		}

		Question question = questionService.findQuestion(questionId);
		if (question == null) {
			return ResponseEntity.noContent().build();
		}
		newAnswer.setQuestion(question);
		Answer savedAnswer = answerService.saveAnswer(newAnswer);

		// Build the URI for the newly created question
		URI locationNewAnswer = ucb.path("api/answer/{id}")
				.buildAndExpand(savedAnswer.getId()).toUri();

		// Return the URI of the newly created question
		return ResponseEntity.created(locationNewAnswer).build();
	}

	/**
	 * Updates an answer.
	 * 
	 * @param answerId     The ID of the answer to update.
	 * @param answerUpdate The updated answer object.
	 * @return ResponseEntity indicating success or failure of update.
	 */
	@PutMapping("/answer/{answerId}")
	private ResponseEntity<Void> putAnswer(@PathVariable("answerId") Long answerId,
			@RequestBody Answer answerUpdate) {

		Answer answerToUpdate = answerService.getAnswerById(answerId).orElse(null);
		if (answerToUpdate != null) {
			// We check that the uuids match
			if (!answerToUpdate.getUserUuid().equals(answerUpdate.getUserUuid())) {
				return ResponseEntity.badRequest().build();
			}
			Answer updatedAnswer = new Answer(answerId, answerUpdate.getRating(),
					answerToUpdate.getQuestion(), answerUpdate.getUserUuid());
			answerService.saveAnswer(updatedAnswer);
		}
		return ResponseEntity.noContent().build();

	}

	/**
	 * Deletes an answer by its ID.
	 * 
	 * @param answerId The ID of the answer to delete.
	 * @return ResponseEntity indicating success or failure of deletion.
	 */
	@DeleteMapping("/answer/{answerId}")
	private ResponseEntity<Void> deleteAnswer(@PathVariable("answerId") Long answerId) {
		Answer answerToDelete = answerService.getAnswerById(answerId).orElse(null);
		if (answerToDelete != null) {
			answerService.deleteAnswer(answerId);
		}
		return ResponseEntity.noContent().build();
	}

	/**
	 * Deletes all answers associated with a question.
	 * 
	 * @param questionId The ID of the question.
	 * @return ResponseEntity indicating success or failure of deletion.
	 */
	@DeleteMapping("/question/{questionId}/answer")
	private ResponseEntity<Void> deleteAllAnswersFromQuestion(@PathVariable("questionId") Long questionId) {

		Question questionToClear = questionService.findQuestion(questionId);

		if (questionToClear == null) {
			return ResponseEntity.noContent().build();
		}

		answerService.deleteByQuestionId(questionId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Retrieves all answers for a survey.
	 * 
	 * @param surveyId The ID of the survey.
	 * @return ResponseEntity containing answers for the survey if found, otherwise
	 *         returns no content.
	 */
	@GetMapping("/surveys/{surveyId}/answer")
	public ResponseEntity<Iterable<Iterable<Answer>>> getAllAnswersForSurvey(@PathVariable("surveyId") Long surveyId) {
		try {
			return ResponseEntity.ok(responseService.getAllAnswersForSurvey(surveyId));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Retrieves full survey results summary (including all the users).
	 * 
	 * @param surveyId The ID of the survey.
	 * @return ResponseEntity containing full survey results summary if found,
	 *         otherwise returns no content.
	 */
	@GetMapping("/surveys/{surveyId}/results/full")
	public ResponseEntity<SurveyResultDTO> getSurveyResultsFullSummary(@PathVariable("surveyId") Long surveyId) {

		try {
			return ResponseEntity.ok(responseService.getSurveyResultsFullSummary(surveyId));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Retrieves survey results sorted by user.
	 * 
	 * @param surveyId The ID of the survey.
	 * @return ResponseEntity containing survey results by user if found, otherwise
	 *         returns no content.
	 */
	@GetMapping("/surveys/{surveyId}/results")
	public ResponseEntity<List<UserResultDTO>> getSurveyResultsByUser(@PathVariable("surveyId") Long surveyId) {

		try {
			return ResponseEntity.ok(responseService.getSurveyResultsByUser(surveyId));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Creates a submission for a survey.
	 * 
	 * @param surveyId          The ID of the survey.
	 * @param newUserSubmission The submission details.
	 * @return ResponseEntity indicating success or failure of submission.
	 */
	@CrossOrigin("http://localhost:5173")
	@PostMapping(value = "/surveys/{surveyId}/submit", consumes = "application/json")
	public ResponseEntity<String> createSubmission(@PathVariable("surveyId") Long surveyId,
			@RequestBody UserSubmission newUserSubmission) {

		UUID userUuid = newUserSubmission.getUserUuid();

		// We reject the request if the answer does not have a user uuid present
		if (userUuid == null) {
			return ResponseEntity.badRequest().body("Invalid request: user uuid not present");
		}

		// We reject if the user already answered the survey
		if (responseService.hasAnswersFromUserUuidToSurvey(userUuid, surveyId)) {
			return ResponseEntity.badRequest().body("Invalid request: survey already answered");
		}

		try {
			responseService.processSubmission(userUuid, newUserSubmission.getSubmissions());
			return ResponseEntity.ok().build();
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

}
