package com.milkevich.exception;

public class AddressDataNotFoundException extends RuntimeException {
  public AddressDataNotFoundException(String message) {
	super(message);
  }
}
