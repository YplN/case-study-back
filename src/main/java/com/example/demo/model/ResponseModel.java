package com.example.demo.model;

/**
 * Generic class representing a response model.
 * 
 * @param <T> The type of data in the response model.
 */
public class ResponseModel<T> {

	private T data;
	private Long id;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}