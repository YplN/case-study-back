
package com.example.demo.service;

import java.util.Optional;

import com.example.demo.model.Answer;

import org.springframework.stereotype.Service;

import com.example.demo.repository.AnswerRepository;

/**
 * Service class for managing Answer entities.
 */
@Service
public class AnswerService {
	private final AnswerRepository answerRepository;

	public AnswerService(AnswerRepository answerRepository) {
		this.answerRepository = answerRepository;
	}

	/**
	 * Retrieves all answers.
	 *
	 * @return Iterable collection of all answers.
	 */
	public Iterable<Answer> getAllAnswers() {
		return answerRepository.findAll();
	}

	/**
	 * Retrieves an answer by its ID.
	 *
	 * @param id The ID of the answer.
	 * @return Optional containing the answer if found, empty otherwise.
	 */
	public Optional<Answer> getAnswerById(Long id) {
		if (id == null) {
			return Optional.empty();
		}
		return answerRepository.findById(id);
	}

	/**
	 * Saves an answer.
	 *
	 * @param answer The answer to be saved.
	 * @return The saved answer.
	 * @throws IllegalArgumentException If the answer is null.
	 */
	public Answer saveAnswer(Answer answer) {
		if (answer == null) {
			throw new IllegalArgumentException("answer cannot be null.");
		}
		return answerRepository.save(answer);
	}

	/**
	 * Deletes an answer by its ID.
	 *
	 * @param id The ID of the answer to be deleted.
	 * @throws IllegalArgumentException If the ID is null.
	 */
	public void deleteAnswer(Long id) throws IllegalArgumentException {
		if (id == null) {
			throw new IllegalArgumentException("id cannot be null.");
		}
		answerRepository.deleteById(id);
	}

	/**
	 * Finds answers by question ID.
	 *
	 * @param questionId The ID of the question.
	 * @return Iterable collection of answers for the specified question.
	 * @throws IllegalArgumentException If the question ID is null.
	 */
	public Iterable<Answer> findByQuestionId(Long questionId) throws IllegalArgumentException {
		if (questionId == null) {
			throw new IllegalArgumentException("Question id cannot be null.");
		}
		;
		return answerRepository.findByQuestionId(questionId);
	}

	/**
	 * Deletes answers by question ID.
	 *
	 * @param questionId The ID of the question.
	 * @throws IllegalArgumentException If the question ID is null.
	 */
	public void deleteByQuestionId(Long questionId) {
		if (questionId == null) {
			throw new IllegalArgumentException("Question id cannot be null.");
		}
		answerRepository.deleteByQuestionId(questionId);
	}

}
