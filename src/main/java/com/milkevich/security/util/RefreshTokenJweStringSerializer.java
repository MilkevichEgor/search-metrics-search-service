package com.milkevich.security.util;

import com.milkevich.security.model.RefreshToken;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import java.util.Date;
import java.util.function.Function;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
public class RefreshTokenJweStringSerializer implements Function<RefreshToken, String> {

  private final JWEEncrypter jweEncrypter;
  private JWEAlgorithm jweAlgorithm = JWEAlgorithm.DIR;
  private EncryptionMethod encryptionMethod = EncryptionMethod.A192GCM;

  public RefreshTokenJweStringSerializer(JWEEncrypter jweEncrypter) {
	this.jweEncrypter = jweEncrypter;
  }

  public RefreshTokenJweStringSerializer(JWEEncrypter jweEncrypter,
										 JWEAlgorithm jweAlgorithm,
										 EncryptionMethod encryptionMethod) {
	this.jweEncrypter = jweEncrypter;
	this.jweAlgorithm = jweAlgorithm;
	this.encryptionMethod = encryptionMethod;
  }

  @Override
  public String apply(RefreshToken refreshToken) {
	JWEHeader jweHeader = new JWEHeader.Builder(this.jweAlgorithm, this.encryptionMethod)
		.keyID(refreshToken.id().toString())
		.build();

	JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
		.jwtID(refreshToken.id().toString())
		.subject(refreshToken.subject())
		.issueTime(Date.from(refreshToken.createdAt()))
		.expirationTime(Date.from(refreshToken.expiresAt()))
		.claim("authorities", refreshToken.authorities())
		.build();

	EncryptedJWT encryptedJWT = new EncryptedJWT(jweHeader, jwtClaimsSet);
	try {
	  encryptedJWT.encrypt(this.jweEncrypter);

	  return encryptedJWT.serialize();
	} catch (JOSEException e) {
	  log.error(e.getMessage(), e);
	}
	return null;
  }
}
