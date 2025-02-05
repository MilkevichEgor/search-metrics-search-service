package com.geosearch.security;

import com.geosearch.security.model.AccessToken;
import com.geosearch.security.model.RefreshToken;
import jakarta.servlet.http.HttpServletRequest;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@RequiredArgsConstructor
public class JwtAuthenticationConverter implements AuthenticationConverter {

  private final Function<String, AccessToken> accessTokenStringDeserializer;
  private final Function<String, RefreshToken> refreshTokenStringDeserializer;

  @Override
  public Authentication convert(HttpServletRequest request) {
	String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
	if (authorization != null && authorization.startsWith("Bearer ")) {
	  String token = authorization.substring(7, authorization.length()).trim();
	  AccessToken accessToken = this.accessTokenStringDeserializer.apply(token);

	  if (accessToken != null) {
		return new PreAuthenticatedAuthenticationToken(accessToken, token);
	  }

	  RefreshToken refreshToken = this.refreshTokenStringDeserializer.apply(token);
	  if (refreshToken != null) {
		return new PreAuthenticatedAuthenticationToken(refreshToken, token);
	  }
	}
	return null;
  }
}
