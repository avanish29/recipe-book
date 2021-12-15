package com.recipebook.domain.values;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class APIError {
	private HttpStatus status;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
	private LocalDateTime timestamp;
	private String message;
	private String debugMessage;

	public APIError() {
		timestamp = LocalDateTime.now();
	}

	public APIError(HttpStatus status) {
		this();
		this.status = status;
	}

	public APIError(HttpStatus status, Throwable ex) {
		this();
		this.status = status;
		this.message = "Unexpected error";
		if(ex != null) this.debugMessage = ex.getLocalizedMessage();
	}

	public APIError(HttpStatus status, String message, Throwable ex) {
		this();
		this.status = status;
		this.message = message;
		if(ex != null) this.debugMessage = ex.getLocalizedMessage();
	}
	
	public Integer getStatusCode() {
		return status != null ? status.value() : null;
	}
}
