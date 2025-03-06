package com.milkevich.security.util;

import com.milkevich.security.model.AccessToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.util.Date;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccessTokenJwsStringSerializer implements Function<AccessToken, String> {

  private final JWSSigner jwsSigner;
  private final JWSAlgorithm jwsAlgorithm = JWSAlgorithm.HS256;

  public AccessTokenJwsStringSerializer(JWSSigner jwsSigner) {
	this.jwsSigner = jwsSigner;
  }

  @Override
  public String apply(AccessToken accessToken) {
	JWSHeader jwsHeader = new JWSHeader.Builder(this.jwsAlgorithm)
		.keyID(accessToken.id().toString())
		.build();

	JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
		.jwtID(accessToken.id().toString())
		.subject(accessToken.subject())
		.issueTime(Date.from(accessToken.createdAt()))
		.expirationTime(Date.from(accessToken.expiresAt()))
		.claim("authorities", accessToken.authorities())
		.build();

	SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);
	try {
	  signedJWT.sign(this.jwsSigner);

	  return signedJWT.serialize();
	} catch (JOSEException e) {
	  log.error(e.getMessage(), e);
	}

	return null;
  }
}
