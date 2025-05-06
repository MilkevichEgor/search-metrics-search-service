package com.milkevich.exception.handler;

import com.milkevich.dto.response.ResponseExceptionMessage;
import com.milkevich.exception.AddressDataNotFoundException;
import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestErrorHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler(AddressDataNotFoundException.class)
  public ResponseEntity<ResponseExceptionMessage> handleAddressDataNotFoundException(AddressDataNotFoundException ex, WebRequest request) {
	ResponseExceptionMessage exceptionMessage = new ResponseExceptionMessage(new Date(), ex.getMessage(), request.getDescription(false));
	return new ResponseEntity<>(exceptionMessage, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ResponseExceptionMessage> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
	ResponseExceptionMessage exceptionMessage = new ResponseExceptionMessage(new Date(), "Authentication Error: " + ex.getMessage(), request.getDescription(false));
	return new ResponseEntity<>(exceptionMessage, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ResponseExceptionMessage> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
	ResponseExceptionMessage exceptionMessage = new ResponseExceptionMessage(new Date(), "Access denied: " + ex.getMessage(), request.getDescription(false));
	return new ResponseEntity<>(exceptionMessage, HttpStatus.FORBIDDEN);
  }
}