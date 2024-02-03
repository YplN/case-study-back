package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Question;

import jakarta.transaction.Transactional;

/**
 * Repository interface for managing Question entities.
 */
@Repository
public interface QuestionRepository extends CrudRepository<Question, Long> {

	Iterable<Question> findBySurveyId(Long surveyId);

	@Transactional
	void deleteBySurveyId(Long surveyId);

}
