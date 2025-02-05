package com.geosearch.security;

import com.geosearch.repository.InactiveTokenRepository;
import com.geosearch.security.model.AccessToken;
import com.geosearch.security.model.RefreshToken;
import com.geosearch.security.model.RefreshTokenUser;
import com.geosearch.security.model.TokenUser;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@RequiredArgsConstructor
public class TokenAuthenticationUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

  private final InactiveTokenRepository inactiveTokenRepository;

  @Override
  public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken authenticationToken) throws UsernameNotFoundException {
	if (authenticationToken.getPrincipal() instanceof AccessToken accessToken) {
	  return new TokenUser(accessToken.subject(), "nopassword", true, true,
		  !inactiveTokenRepository.existsById(accessToken.id()) &&
			  accessToken.expiresAt().isAfter(Instant.now()), true,
		  accessToken.authorities().stream()
			  .map(SimpleGrantedAuthority::new)
			  .toList(), accessToken);

	} else if (authenticationToken.getPrincipal() instanceof RefreshToken refreshToken) {
	  return new RefreshTokenUser(refreshToken.subject(), "nopassword", true, true,
		  !inactiveTokenRepository.existsById(refreshToken.id()) &&
			  refreshToken.expiresAt().isAfter(Instant.now()), true,
		  refreshToken.authorities().stream()
			  .map(SimpleGrantedAuthority::new)
			  .toList(), refreshToken);
	}

	throw new UsernameNotFoundException("Пользователя с username: %s, не найдено".formatted(authenticationToken.getName()));
  }
}
