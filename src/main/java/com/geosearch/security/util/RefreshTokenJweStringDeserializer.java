package com.geosearch.security.util;

import com.geosearch.security.model.RefreshToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import java.text.ParseException;
import java.util.UUID;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class RefreshTokenJweStringDeserializer implements Function<String, RefreshToken> {

  private final JWEDecrypter jweDecrypter;

  @Override
  public RefreshToken apply(String string) {
	try {
	  EncryptedJWT encryptedJWT = EncryptedJWT.parse(string);
	  encryptedJWT.decrypt(this.jweDecrypter);

	  JWTClaimsSet claimsSet = encryptedJWT.getJWTClaimsSet();
	  return new RefreshToken(UUID.fromString(claimsSet.getJWTID()),
		  claimsSet.getSubject(),
		  claimsSet.getStringListClaim("authorities"),
		  claimsSet.getIssueTime().toInstant(),
		  claimsSet.getExpirationTime().toInstant());
	} catch (ParseException | JOSEException e) {
	  log.error(e.getMessage(), e);
	}
	return null;
  }
}
