package com.geosearch.security.factory;

import com.geosearch.security.model.RefreshToken;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@Setter
public class DefaultRefreshTokenFactory implements Function<Authentication, RefreshToken> {

  private Duration tokenTtl = Duration.ofDays(1);

  @Override
  public RefreshToken apply(Authentication authentication) {
	List<String> authorities = new LinkedList<>();
	authorities.add("JWT_REFRESH");
	authorities.add("JWT_LOGOUT");
	authentication.getAuthorities()
		.stream()
		.map(GrantedAuthority::getAuthority)
		.map(authority -> "GRANT_" + authority)
		.forEach(authorities::add);

	Instant now = Instant.now();
	return new RefreshToken(UUID.randomUUID(), authentication.getName(), authorities, now, now.plus(this.tokenTtl));
  }
}
