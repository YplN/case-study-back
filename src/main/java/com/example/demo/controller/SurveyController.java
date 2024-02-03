package com.example.demo.controller;

import com.example.demo.model.ResponseModel;
import com.example.demo.model.Survey;
import com.example.demo.service.ResponseService;
import com.example.demo.service.SurveyService;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Controller class for handling survey-related endpoints.
 */
@RestController
@RequestMapping("/api")
public class SurveyController {
	@Autowired
	private final SurveyService surveyService;
	private final ResponseService responseService;

	/**
	 * Constructor for SurveyController.
	 * 
	 * @param surveyService   Service for managing surveys.
	 * @param responseService Service for managing responses.
	 */
	public SurveyController(SurveyService surveyService,
			ResponseService responseService) {
		this.surveyService = surveyService;
		this.responseService = responseService;
	}

	/**
	 * Retrieves a survey by its ID.
	 * 
	 * @param surveyId The ID of the survey.
	 * @return ResponseEntity containing the survey if found, otherwise returns
	 *         no content.
	 */
	@GetMapping("/surveys/{surveyId}")
	public ResponseEntity<Survey> getSurveyById(@PathVariable("surveyId") Long surveyId) {
		Survey survey = surveyService.findSurvey(surveyId);
		if (survey == null) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.ok(survey);
		}
	}

	/**
	 * Retrieves all surveys.
	 * 
	 * @return Iterable of all surveys.
	 */
	@GetMapping("/surveys")
	public ResponseEntity<Iterable<Survey>> getAllSurveys() {
		return ResponseEntity.ok(surveyService.getAllSurveys());
	}

	/**
	 * Retrieves all surveys associated with a user's UUID.
	 * 
	 * @param userUuid The UUID of the user.
	 * @return ResponseEntity containing surveys sorted for a specific UUID.
	 */
	@CrossOrigin("http://localhost:5173")
	@GetMapping(path = "/surveys/user/{userUuid}", produces = "application/json")
	public ResponseEntity<Object> getAllSurveysFromUserUuid(@PathVariable("userUuid") UUID userUuid) {
		return ResponseEntity.ok(responseService.getSurveysSortedForUserUuid(userUuid));
	}

	/**
	 * Retrieves all surveys in JSON format.
	 * 
	 * @return Iterable of ResponseModel containing survey details.
	 */
	@GetMapping(path = "/surveys/json", produces = "application/json")
	public ResponseEntity<Iterable<ResponseModel<Survey>>> getAllSurveysJson() {
		return ResponseEntity.ok(responseService.getAllSurveyJson());
	}

	/**
	 * Creates a new survey.
	 * 
	 * @param newSurvey The new survey to be created.
	 * @param ucb       UriComponentsBuilder for building URI.
	 * @return ResponseEntity with the URI of the newly created survey.
	 */
	@PostMapping("/surveys")
	public ResponseEntity<Void> createSurvey(@RequestBody Survey newSurvey, UriComponentsBuilder ucb) {
		Survey savedSurvey = surveyService.saveSurvey(new Survey(newSurvey.getTitle(), newSurvey.getDesc()));
		URI locationNewSurvey = ucb.path("api/surveys/{id}").buildAndExpand(savedSurvey.getId()).toUri();

		return ResponseEntity.created(locationNewSurvey).build();
	}

	/**
	 * Deletes a survey by its ID.
	 * 
	 * @param surveyId The ID of the survey to delete.
	 * @return ResponseEntity indicating success or failure of deletion.
	 */
	@DeleteMapping("/surveys/{surveyId}")
	private ResponseEntity<Void> deleteSurveyById(@PathVariable("surveyId") Long surveyId) {
		try {
			surveyService.deleteSurvey(surveyId);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}

	}

}
