package com.example.demo.service;

import java.util.Optional;
import com.example.demo.model.Question;

import org.springframework.stereotype.Service;

import com.example.demo.repository.QuestionRepository;

/**
 * Service class for managing Question entities.
 */
@Service
public class QuestionService {
	private final QuestionRepository questionRepository;

	public QuestionService(QuestionRepository questionRepository) {
		this.questionRepository = questionRepository;
	}

	/**
	 * Retrieves all questions.
	 *
	 * @return Iterable collection of all questions.
	 */
	public Iterable<Question> getAllQuestions() {
		return questionRepository.findAll();
	}

	/**
	 * Retrieves a question by its ID.
	 *
	 * @param id The ID of the question.
	 * @return Optional containing the question if found, empty otherwise.
	 */
	public Optional<Question> getQuestionById(Long id) {
		if (id == null) {
			return Optional.empty();
		}
		return questionRepository.findById(id);
	}

	/**
	 * Saves a question.
	 *
	 * @param question The question to be saved.
	 * @return The saved question.
	 * @throws IllegalArgumentException If the question is null.
	 */
	public Question saveQuestion(Question question) {
		if (question == null) {
			throw new IllegalArgumentException("question cannot be null.");
		}
		return questionRepository.save(question);
	}

	/**
	 * Deletes a question by its ID.
	 *
	 * @param id The ID of the question to be deleted.
	 * @throws IllegalArgumentException If the ID is null.
	 */
	public void deleteQuestion(Long id) throws IllegalArgumentException {
		if (id == null) {
			throw new IllegalArgumentException("id cannot be null.");
		}
		questionRepository.deleteById(id);
	}

	/**
	 * Finds questions by survey ID.
	 *
	 * @param surveyId The ID of the survey.
	 * @return Iterable collection of questions for the specified survey.
	 * @throws IllegalArgumentException If the survey ID is null.
	 */
	public Iterable<Question> findBySurveyId(Long surveyId) throws IllegalArgumentException {
		if (surveyId == null) {

			throw new IllegalArgumentException("Survey id cannot be null.");
		}
		;
		return questionRepository.findBySurveyId(surveyId);
	}

	/**
	 * Deletes questions by survey ID.
	 *
	 * @param surveyId The ID of the survey.
	 * @throws IllegalArgumentException If the survey ID is null.
	 */
	public void deleteBySurveyId(Long surveyId) {
		if (surveyId == null) {

			throw new IllegalArgumentException("Survey id cannot be null.");
		}
		questionRepository.deleteBySurveyId(surveyId);
	}

	/**
	 * Finds a question by its ID.
	 *
	 * @param id The ID of the question.
	 * @return The found question or null if not found.
	 */
	public Question findQuestion(Long id) {
		return getQuestionById(id).orElse(null);
	}

}
