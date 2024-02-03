package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Answer;
import com.example.demo.model.Question;
import com.example.demo.model.ResponseModel;
import com.example.demo.model.Submission;
import com.example.demo.model.Survey;
import com.example.demo.model.SurveyResultDTO;
import com.example.demo.model.UserResultDTO;

/**
 * Service class for managing survey responses.
 */
@Service
public class ResponseService {

	private final AnswerService answerService;
	private final QuestionService questionService;
	private final SurveyService surveyService;

	public ResponseService(AnswerService answerService, QuestionService questionService, SurveyService surveyService) {
		this.answerService = answerService;
		this.questionService = questionService;
		this.surveyService = surveyService;
	}

	/**
	 * Retrieves all answers of a survey from a specific user.
	 *
	 * @param surveyId The ID of the survey.
	 * @param userUuid The UUID of the user.
	 * @return List of answers.
	 */
	public List<Answer> getAllAnswersOfSurveyFromUserUuid(Long surveyId, UUID userUuid) {
		if (surveyId == null || userUuid == null) {
			return Collections.emptyList();
		}

		Iterable<Question> questions = questionService.findBySurveyId(surveyId);
		List<Answer> filteredAnswers = new ArrayList<>();

		// Iterate over questions and filter answers for the given userUuid
		for (Question question : questions) {
			Iterable<Answer> answers = answerService.findByQuestionId(question.getId());
			for (Answer answer : answers) {
				if (answer.getUserUuid().equals(userUuid)) {
					filteredAnswers.add(answer);
				}
			}
		}

		return filteredAnswers;
	}

	/**
	 * Retrieves all answers from a specific user.
	 *
	 * @param userUuid The UUID of the user.
	 * @return List of answers.
	 */
	public List<Answer> getAllAnswersFromUserUuid(UUID userUuid) {
		if (userUuid == null) {
			return Collections.emptyList();
		}

		Iterable<Survey> surveys = surveyService.getAllSurveys();
		List<Answer> filteredAnswers = new ArrayList<>();

		for (Survey survey : surveys) {
			filteredAnswers.addAll(getAllAnswersOfSurveyFromUserUuid(survey.getId(), userUuid));
		}

		return filteredAnswers;
	}

	/**
	 * Checks if a user has answered a survey.
	 *
	 * @param userUuid The UUID of the user.
	 * @param surveyId The ID of the survey.
	 * @return True if the user has answered the survey, false otherwise.
	 */
	public boolean hasAnswersFromUserUuidToSurvey(UUID userUuid, Long surveyId) {
		return getAllAnswersOfSurveyFromUserUuid(surveyId, userUuid).size() > 0;
	}

	public boolean hasAnswersFromUserUuidToQuestion(UUID userUuid, Long questionId) {
		List<Answer> allAnswer = getAllAnswersFromUserUuid(userUuid);
		return allAnswer.stream().anyMatch(answer -> answer.getQuestion().getId().equals(questionId));
	}

	/**
	 * Processes a submission, adding answers to the database.
	 *
	 * @param userUuid    The UUID of the user submitting responses.
	 * @param submissions List of submissions.
	 * @throws ResourceNotFoundException If a question associated with a submission
	 *                                   is not found in the database.
	 */
	public void processSubmission(UUID userUuid, List<Submission> submissions) throws Exception {

		// We store all the answers and check all the submissions before adding them to
		// the database so if there is a problem with one of them we reject everything.
		List<Answer> answersToAddToTheDatabase = new ArrayList<Answer>();

		for (Submission submission : submissions) {
			Long questionId = submission.getQuestionId();
			Integer rating = submission.getRating();

			// Check if the question is still in the database otherwise we reject everything
			// (nothing was add to the database)
			Question question = questionService.findQuestion(questionId);
			if (question == null) {
				throw new ResourceNotFoundException("Question associated with the submission not in the database.");
			}

			Answer newAnswer = new Answer();
			newAnswer.setRating(rating);
			newAnswer.setQuestion(question);
			newAnswer.setUserUuId(userUuid);

			answersToAddToTheDatabase.add(newAnswer);
		}

		// Everything seems to be ok, we add all the answers to the database
		for (Answer answer : answersToAddToTheDatabase) {
			answerService.saveAnswer(answer);
		}

	}

	/**
	 * Retrieves surveys sorted for a user.
	 *
	 * @param userUuid The UUID of the user.
	 * @return A map containing sorted surveys with the keys "answered" and
	 *         "notAnswered" containing the list of surveys the user answered and
	 *         did not answered respectively.
	 */
	public HashMap<String, Iterable<Survey>> getSurveysSortedForUserUuid(UUID userUuid) {

		Iterable<Survey> allSurveys = surveyService.getAllSurveys();

		ArrayList<Survey> surveysAnswered = new ArrayList<Survey>();
		ArrayList<Survey> surveysNotAnswered = new ArrayList<Survey>();

		for (Survey survey : allSurveys) {
			if (hasAnswersFromUserUuidToSurvey(userUuid, survey.getId())) {
				surveysAnswered.add(survey);
			} else {
				surveysNotAnswered.add(survey);
			}
		}

		HashMap<String, Iterable<Survey>> sortedSurveys = new HashMap<String, Iterable<Survey>>();

		sortedSurveys.put("answered", surveysAnswered);
		sortedSurveys.put("notAnswered", surveysNotAnswered);

		return sortedSurveys;

	}

	/**
	 * Retrieves the survey if the user did not answered it before, throws an
	 * exception otherwise.
	 * 
	 * @param surveyId The id of the survey.
	 * @param userUuid The UUID of the user.
	 * @return a survey with id surveyId if there is no answers with uuid
	 *         corresponding to userUuid
	 * @throws IllegalAccessException
	 */
	public Survey getSurveyForUserUuid(Long surveyId, UUID userUuid) throws IllegalAccessException {

		if (hasAnswersFromUserUuidToSurvey(userUuid, surveyId)) {
			throw new IllegalAccessException("Survey already answered.");
		}

		return surveyService.findSurvey(surveyId);

	}

	/**
	 * Returns the list of the surveys in ResponseModel format
	 * 
	 * @return
	 */
	public ArrayList<ResponseModel<Survey>> getAllSurveyJson() {
		Iterable<Survey> surveys = surveyService.getAllSurveys();
		ArrayList<ResponseModel<Survey>> responses = new ArrayList<ResponseModel<Survey>>();
		for (Survey survey : surveys) {
			ResponseModel<Survey> surveyResponseModel = new ResponseModel<Survey>();
			surveyResponseModel.setData(survey);
			surveyResponseModel.setId(survey.getId());
			responses.add(surveyResponseModel);
		}

		return responses;
	}

	public Iterable<ResponseModel<Question>> getAllQuestionsJson() {
		Iterable<Question> questions = questionService.getAllQuestions();

		ArrayList<ResponseModel<Question>> responses = new ArrayList<ResponseModel<Question>>();
		for (Question question : questions) {
			ResponseModel<Question> questionResponseModel = new ResponseModel<Question>();

			questionResponseModel.setData(question);
			questionResponseModel.setId(question.getId());
			responses.add(questionResponseModel);
		}

		return responses;
	}

	/**
	 * Retrieves all questions in a survey.
	 *
	 * @param surveyId The ID of the survey.
	 * @return Iterable collection of questions.
	 * @throws IllegalArgumentException If the survey ID is null or the survey does
	 *                                  not exist.
	 */
	public Iterable<Question> getQuestionsFromSurveyId(Long surveyId) throws IllegalArgumentException {
		Survey survey = surveyService.findSurvey(surveyId);

		if (survey == null) {
			throw new IllegalArgumentException();
		}
		return questionService.findBySurveyId(surveyId);
	}

	/**
	 * Retrieves all answers for a survey.
	 *
	 * @param surveyId The ID of the survey.
	 * @return List of answer collections.
	 */
	public ArrayList<Iterable<Answer>> getAllAnswersForSurvey(Long surveyId) {

		try {
			Iterable<Question> questions = getQuestionsFromSurveyId(surveyId);
			ArrayList<Iterable<Answer>> allAnswersForSurvey = new ArrayList<Iterable<Answer>>();

			for (Question question : questions) {
				Iterable<Answer> answersForQuestions = answerService.findByQuestionId(question.getId());
				allAnswersForSurvey.add(answersForQuestions);
			}

			return allAnswersForSurvey;

		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}

	}

	/**
	 * Retrieves full survey results summary.
	 *
	 * @param surveyId The ID of the survey.
	 * @return Survey result DTO.
	 * @throws IllegalArgumentException If the survey ID is null or the survey does
	 *                                  not exist.
	 */
	public SurveyResultDTO getSurveyResultsFullSummary(Long surveyId) throws IllegalArgumentException {

		try {
			Iterable<Question> questions = getQuestionsFromSurveyId(surveyId);

			ArrayList<Answer> allAnswersForSurvey = new ArrayList<Answer>();
			ArrayList<Question> allQuestionsForSurvey = new ArrayList<Question>();

			for (Question question : questions) {
				allQuestionsForSurvey.add(question);
				Iterable<Answer> answersForQuestions = answerService.findByQuestionId(question.getId());
				for (Answer answer : answersForQuestions) {
					allAnswersForSurvey.add(answer);
				}
			}

			return surveyService.transformSurveyResultsToDTO(allAnswersForSurvey,
					allQuestionsForSurvey);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * Retrieves survey results by user.
	 *
	 * @param surveyId The ID of the survey.
	 * @return List of user result DTOs.
	 * @throws IllegalArgumentException If the survey ID is null or the survey does
	 *                                  not exist.
	 */
	public List<UserResultDTO> getSurveyResultsByUser(Long surveyId) throws IllegalArgumentException {
		try {
			Iterable<Question> questions = getQuestionsFromSurveyId(surveyId);
			Map<UUID, List<Answer>> allUserUUIDMap = new HashMap<UUID, List<Answer>>();

			for (Question question : questions) {
				Iterable<Answer> answersForQuestions = answerService.findByQuestionId(question.getId());
				for (Answer answer : answersForQuestions) {
					UUID userUuid = answer.getUserUuid();
					List<Answer> userAnswers = allUserUUIDMap.getOrDefault(userUuid, new ArrayList<Answer>());

					if (userAnswers.size() == 0) {
						allUserUUIDMap.put(userUuid, userAnswers);
					}
					userAnswers.add(answer);
				}
			}

			return surveyService.transformSurveyResultsToDTOForUsers(allUserUUIDMap);

		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
}
