package com.geosearch.security;

import com.geosearch.repository.InactiveTokenRepository;
import com.geosearch.security.model.Token;
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
	if (authenticationToken.getPrincipal() instanceof Token token) {
	  return new TokenUser(token.subject(), "nopassword", true, true,
		  !inactiveTokenRepository.existsById(token.id()) &&
		  token.expiresAt().isAfter(Instant.now()), true,
		  token.authorities().stream()
			  .map(SimpleGrantedAuthority::new)
			  .toList(), token);
	}

	throw new UsernameNotFoundException("");
  }
}
