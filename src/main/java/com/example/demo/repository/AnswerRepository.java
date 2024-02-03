package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Answer;

import jakarta.transaction.Transactional;

/**
 * Repository interface for managing Answer entities.
 */
@Repository
public interface AnswerRepository extends CrudRepository<Answer, Long> {

	Iterable<Answer> findByQuestionId(Long questionId);

	@Transactional
	void deleteByQuestionId(Long questionId);

}
