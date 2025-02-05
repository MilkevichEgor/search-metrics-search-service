package com.geosearch.security.factory;

import com.geosearch.security.model.Token;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;
import lombok.Setter;

@Setter
public class DefaultAccessTokenFactory implements Function<Token, Token> {

  private Duration tokenTtl = Duration.ofMinutes(5);

  @Override
  public Token apply(Token refreshToken) {
	Instant now = Instant.now();

	return new Token(refreshToken.id(), refreshToken.subject(),
		refreshToken.authorities().stream()
			.filter(authority -> authority.startsWith("GRANT_"))
			.map(authority -> authority.substring(6, authority.length()))
			.toList(), now, now.plus(this.tokenTtl));
  }
}
