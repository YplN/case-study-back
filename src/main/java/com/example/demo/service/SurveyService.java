package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.model.Answer;
import com.example.demo.model.AnswerDTO;
import com.example.demo.model.Question;
import com.example.demo.model.QuestionDTO;
import com.example.demo.model.ResultDTO;
import com.example.demo.model.Survey;
import com.example.demo.model.SurveyResultDTO;
import com.example.demo.model.UserResultDTO;
import com.example.demo.repository.SurveyRepository;

/**
 * Service class for managing surveys.
 */
@Service
public class SurveyService {

	private final SurveyRepository surveyRepository;

	public SurveyService(SurveyRepository surveyRepository) {
		this.surveyRepository = surveyRepository;
	}

	/**
	 * Retrieves all surveys.
	 * 
	 * @return Iterable collection of surveys.
	 */
	public Iterable<Survey> getAllSurveys() {
		return surveyRepository.findAll();
	}

	/**
	 * Retrieves a survey by its ID.
	 * 
	 * @param id The ID of the survey.
	 * @return Optional containing the survey, or empty if not found.
	 */
	public Optional<Survey> getSurveyById(Long id) {
		if (id == null) {
			return Optional.empty();
		}
		return surveyRepository.findById(id);
	}

	/**
	 * Saves a survey.
	 * 
	 * @param survey The survey to save.
	 * @return The saved survey.
	 * @throws IllegalArgumentException If the survey is null.
	 */
	public Survey saveSurvey(Survey survey) {
		if (survey == null) {
			throw new IllegalArgumentException("survey cannot be null.");
		}
		return surveyRepository.save(survey);
	}

	/**
	 * Deletes a survey by its ID.
	 * 
	 * @param id The ID of the survey to delete.
	 * @throws IllegalArgumentException If the ID is null.
	 */
	public void deleteSurvey(Long id) throws IllegalArgumentException {
		if (id == null) {
			throw new IllegalArgumentException("id cannot be null.");
		}
		surveyRepository.deleteById(id);
	}

	/**
	 * Transforms survey results into a DTO for presentation.
	 *
	 * @param allAnswers   List of all answers.
	 * @param allQuestions List of all questions.
	 * @return SurveyResultDTO representing survey results.
	 */
	public SurveyResultDTO transformSurveyResultsToDTO(List<Answer> allAnswers, List<Question> allQuestions) {

		if (allAnswers.size() == 0) {
			return null;
		}

		SurveyResultDTO surveyResultDTO = new SurveyResultDTO();
		surveyResultDTO.setId(allAnswers.get(0).getQuestion().getSurvey().getId());
		surveyResultDTO.setTitle(allAnswers.get(0).getQuestion().getSurvey().getTitle());
		surveyResultDTO.setDesc(allAnswers.get(0).getQuestion().getSurvey().getDesc());
		Map<Long, QuestionDTO> questionMap = new HashMap<>();

		for (Question question : allQuestions) {
			Long questionId = question.getId();
			QuestionDTO questionDTO = questionMap.getOrDefault(questionId, new QuestionDTO());
			if (questionDTO.getId() == null) {
				questionDTO.setId(questionId);
				questionDTO.setText(question.getText());
				questionDTO.setAnswers(new ArrayList<AnswerDTO>());
				questionMap.put(questionId, questionDTO);
			}
		}

		for (Answer answer : allAnswers) {
			Long questionId = answer.getQuestion().getId();
			QuestionDTO questionDTO = questionMap.getOrDefault(questionId, new QuestionDTO());
			AnswerDTO answerDTO = new AnswerDTO();
			answerDTO.setId(answer.getId());
			answerDTO.setRating(answer.getRating());
			answerDTO.setUuid(answer.getUserUuid());
			questionDTO.getAnswers().add(answerDTO);
		}

		surveyResultDTO.setQuestions(new ArrayList<>(questionMap.values()));

		return surveyResultDTO;
	}

	/**
	 * Transforms survey results into DTOs for users.
	 *
	 * @param allUserUUIDsMap Map containing user UUIDs and their associated
	 *                        answers.
	 * @return List of UserResultDTO representing survey results for users.
	 */
	public List<UserResultDTO> transformSurveyResultsToDTOForUsers(Map<UUID, List<Answer>> allUserUUIDsMap) {

		if (allUserUUIDsMap.size() == 0) {
			return new ArrayList<UserResultDTO>();
		}

		ArrayList<UserResultDTO> userResultDTOs = new ArrayList<UserResultDTO>();

		for (UUID userUuid : allUserUUIDsMap.keySet()) {
			UserResultDTO newUserResultDTO = new UserResultDTO();
			newUserResultDTO.setUserUuid(userUuid);

			for (Answer answer : allUserUUIDsMap.get(userUuid)) {
				ResultDTO newResultDTO = new ResultDTO();
				newResultDTO.setIdAnswer(answer.getId());
				newResultDTO.setAnswerRating(answer.getRating());
				newResultDTO.setIdQuestion(answer.getQuestion().getId());
				newResultDTO.setTextQuestion(answer.getQuestion().getText());

				if (newUserResultDTO.getUserAnswers() == null) {
					newUserResultDTO.setUserAnswers(new ArrayList<ResultDTO>());
				}
				newUserResultDTO.getUserAnswers().add(newResultDTO);
			}

			userResultDTOs.add(newUserResultDTO);
		}

		return userResultDTOs;
	}

	/**
	 * Finds a survey by its ID.
	 *
	 * @param id The ID of the survey.
	 * @return The found survey or null if not found.
	 */
	public Survey findSurvey(Long id) {
		return getSurveyById(id).orElse(null);

	}
}
