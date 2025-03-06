package com.milkevich.security.factory;

import com.milkevich.security.model.AccessToken;
import com.milkevich.security.model.RefreshToken;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;
import lombok.Setter;

@Setter
public class DefaultAccessTokenFactory implements Function<RefreshToken, AccessToken> {

  private Duration tokenTtl = Duration.ofMinutes(5);

  @Override
  public AccessToken apply(RefreshToken refreshToken) {
	Instant now = Instant.now();

	return new AccessToken(refreshToken.id(), refreshToken.subject(),
		refreshToken.authorities().stream()
			.filter(authority -> authority.startsWith("GRANT_"))
			.map(authority -> authority.substring(6, authority.length()))
			.toList(), now, now.plus(this.tokenTtl));
  }
}
