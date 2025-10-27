package br.com.physioapp.api.physioapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CrefitoAlreadyExistsException extends RuntimeException {
  
  public CrefitoAlreadyExistsException(String message) {
    super(message);
  }
}
