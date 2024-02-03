package com.example.demo.controller;

import com.example.demo.model.Question;
import com.example.demo.model.ResponseModel;
import com.example.demo.model.Survey;
import com.example.demo.service.QuestionService;
import com.example.demo.service.ResponseService;
import com.example.demo.service.SurveyService;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Controller class for handling question-related endpoints.
 */
@RestController
@RequestMapping("/api")
public class QuestionController {

	private final QuestionService questionService;
	private final SurveyService surveyService;
	private final ResponseService responseService;

	/**
	 * Constructor for QuestionController.
	 * 
	 * @param questionService Service for managing questions.
	 * @param surveyService   Service for managing surveys.
	 * @param responseService Service for managing responses.
	 */
	public QuestionController(QuestionService questionService, SurveyService surveyService,
			ResponseService responseService) {
		this.questionService = questionService;
		this.surveyService = surveyService;
		this.responseService = responseService;
	}

	/**
	 * Creates a new question for a survey.
	 * 
	 * @param surveyId    The ID of the survey.
	 * @param newQuestion The new question to be created.
	 * @param ucb         UriComponentsBuilder for building URI.
	 * @return ResponseEntity with the URI of the newly created question.
	 */
	@PostMapping("/surveys/{surveyId}/question")
	public ResponseEntity<Void> createQuestion(@PathVariable("surveyId") Long surveyId, @RequestBody Question newQuestion,
			UriComponentsBuilder ucb) {

		Survey survey = surveyService.findSurvey(surveyId);

		if (survey == null) {
			return ResponseEntity.noContent().build();
		}

		if (newQuestion.getText() == null) {
			return ResponseEntity.badRequest().build();
		}

		newQuestion.setSurvey(survey);
		Question savedQuestion = questionService.saveQuestion(newQuestion);

		// Build the URI for the newly created question
		URI locationNewQuestion = ucb.path("api/question/{id}")
				.buildAndExpand(savedQuestion.getId()).toUri();

		// Return the URI of the newly created question
		return ResponseEntity.created(locationNewQuestion).build();
	}

	/**
	 * Retrieves all questions.
	 * 
	 * @return ResponseEntity containing all questions if found, otherwise returns
	 *         no content.
	 */
	@GetMapping("/question")
	public ResponseEntity<Iterable<Question>> getAllQuestions() {
		Iterable<Question> questions = questionService.getAllQuestions();
		if (questions == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(questions);
	}

	/**
	 * Retrieves all questions in JSON format.
	 * 
	 * @return Iterable of ResponseModel containing question details.
	 */
	@GetMapping("/question/json")
	public ResponseEntity<Iterable<ResponseModel<Question>>> getAllQuestionsJson() {
		return ResponseEntity.ok(responseService.getAllQuestionsJson());
	}

	/**
	 * Retrieves a question by its ID.
	 * 
	 * @param questionId The ID of the question.
	 * @return ResponseEntity containing the question if found, otherwise returns no
	 *         content.
	 */
	@GetMapping("/question/{questionId}")
	public ResponseEntity<Question> getQuestionById(@PathVariable("questionId") Long questionId) {
		Question question = questionService.findQuestion(questionId);
		if (question == null) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.ok(question);
		}
	}

	/**
	 * Updates a question.
	 * 
	 * @param questionId     The ID of the question to update.
	 * @param questionUpdate The updated question object.
	 * @return ResponseEntity indicating success or failure of update.
	 */
	@PutMapping("/question/{questionId}")
	private ResponseEntity<Void> putQuestion(@PathVariable("questionId") Long questionId,
			@RequestBody Question questionUpdate) {

		Question questionToUpdate = questionService.findQuestion(questionId);
		if (questionToUpdate != null) {
			Question updatedQuestion = new Question(questionId, questionUpdate.getText(),
					questionToUpdate.getSurvey());
			questionService.saveQuestion(updatedQuestion);
		}
		return ResponseEntity.noContent().build();

	}

	/**
	 * Deletes a question by its ID.
	 * 
	 * @param questionId The ID of the question to delete.
	 * @return ResponseEntity indicating success or failure of deletion.
	 */
	@DeleteMapping("/question/{questionId}")
	private ResponseEntity<Void> deleteQuestion(@PathVariable("questionId") Long questionId) {
		Question questionToDelete = questionService.findQuestion(questionId);
		if (questionToDelete != null) {
			questionService.deleteQuestion(questionId);
		}
		return ResponseEntity.noContent().build();
	}

	/**
	 * Deletes all questions associated with a survey.
	 * 
	 * @param surveyId The ID of the survey.
	 * @return ResponseEntity indicating success or failure of deletion.
	 */
	@DeleteMapping("/surveys/{surveyId}/question")
	private ResponseEntity<Void> deleteAllQuestionsFromSurvey(@PathVariable("surveyId") Long surveyId) {
		if (surveyId == null) {
			return ResponseEntity.notFound().build();
		}
		Survey surveyToClear = surveyService.findSurvey(surveyId);

		if (surveyToClear == null) {
			return ResponseEntity.badRequest().build();
		}

		questionService.deleteBySurveyId(surveyId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Retrieves all questions associated with a survey and user UUID.
	 * 
	 * @param surveyId The ID of the survey.
	 * @param userUuid The UUID of the user.
	 * @return ResponseEntity containing questions if found, otherwise returns no
	 *         content.
	 */
	@CrossOrigin("http://localhost:5173")
	@GetMapping(path = "/surveys/{surveyId}/user/{userUuid}", produces = "application/json")
	public ResponseEntity<Object> getAllQuestionsBySurveyIdAndUserUuid(
			@PathVariable("surveyId") Long surveyId,
			@PathVariable("userUuid") UUID userUuid) {

		try {
			Survey survey = responseService.getSurveyForUserUuid(surveyId, userUuid);
			if (survey == null) {
				return ResponseEntity.noContent().build();
			}
			Iterable<Question> questions = questionService.findBySurveyId(surveyId);
			return ResponseEntity.ok(questions);
		} catch (IllegalAccessException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}

	}

	/**
	 * Retrieves all questions associated with a survey.
	 * 
	 * @param surveyId The ID of the survey.
	 * @return ResponseEntity containing questions if found, otherwise returns no
	 *         content.
	 */
	@GetMapping("/surveys/{surveyId}/question")
	public ResponseEntity<Iterable<Question>> getAllQuestionsBySurveyId(@PathVariable("surveyId") Long surveyId) {
		Survey survey = surveyService.findSurvey(surveyId);

		if (survey == null) {
			return ResponseEntity.noContent().build();
		}

		Iterable<Question> questions = questionService.findBySurveyId(surveyId);
		return ResponseEntity.ok(questions);

	}

}