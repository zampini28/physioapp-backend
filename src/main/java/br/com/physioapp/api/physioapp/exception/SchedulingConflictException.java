package br.com.physioapp.api.physioapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SchedulingConflictException extends RuntimeException {
  
  public SchedulingConflictException(String message) {
    super(message);
  }
}
