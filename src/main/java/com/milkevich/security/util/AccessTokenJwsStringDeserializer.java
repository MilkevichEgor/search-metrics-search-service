package com.milkevich.security.util;

import com.milkevich.security.model.AccessToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.UUID;
import java.util.function.Function;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
public class AccessTokenJwsStringDeserializer implements Function<String, AccessToken> {

  private final JWSVerifier jwsVerifier;

  public AccessTokenJwsStringDeserializer(JWSVerifier jwsVerifier) {
	this.jwsVerifier = jwsVerifier;
  }

  @Override
  public AccessToken apply(String string) {
	try {
	  var signedJWT = SignedJWT.parse(string);
	  if (signedJWT.verify(this.jwsVerifier)) {

		JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
		return new AccessToken(UUID.fromString(claimsSet.getJWTID()),
			claimsSet.getSubject(),
			claimsSet.getStringListClaim("authorities"),
			claimsSet.getIssueTime().toInstant(),
			claimsSet.getExpirationTime().toInstant());
	  }
	} catch (ParseException | JOSEException e) {
	  log.error(e.getMessage(), e);
	}
	return null;
  }
}
