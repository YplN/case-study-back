package com.example.demo.repository;

import com.example.demo.model.Survey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

/**
 * Repository interface for managing Survey entities.
 */
@Service
public interface SurveyRepository extends CrudRepository<Survey, Long> {
}
