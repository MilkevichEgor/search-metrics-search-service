package com.geosearch.security;

import com.geosearch.repository.InactiveTokenRepository;
import com.geosearch.security.filter.JwtLogoutFilter;
import com.geosearch.security.filter.RefreshTokenFilter;
import com.geosearch.security.filter.RequestJwtTokensFilter;
import com.geosearch.security.model.Token;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
public class JwtAuthenticationConfigurer extends AbstractHttpConfigurer<JwtAuthenticationConfigurer, HttpSecurity> {

  private Function<Token, String> refreshTokenStringSerializer = Objects::toString;
  private Function<Token, String> accessTokenStringSerializer = Objects::toString;

  private Function<String, Token> accessTokenStringDeserializer;
  private Function<String, Token> refreshTokenStringDeserializer;

  private InactiveTokenRepository inactiveTokenRepository;

  @Override
  public void init(HttpSecurity builder) throws Exception {
	var csrfConfigurer = builder.getConfigurer(CsrfConfigurer.class);
	if (csrfConfigurer != null) {
	  csrfConfigurer.ignoringRequestMatchers(new AntPathRequestMatcher("/jwt/tokens", "POST"));
	}
  }

  @Override
  public void configure(HttpSecurity builder) throws Exception {
	RequestJwtTokensFilter requestJwtTokensFilter = new RequestJwtTokensFilter();
	requestJwtTokensFilter.setAccessTokenStringSerializer(this.accessTokenStringSerializer);
	requestJwtTokensFilter.setRefreshTokenStringSerializer(this.refreshTokenStringSerializer);

	AuthenticationFilter jwtAuthenticationFilter = new AuthenticationFilter(builder.getSharedObject(AuthenticationManager.class),
		new JwtAuthenticationConverter(this.accessTokenStringDeserializer, this.refreshTokenStringDeserializer));
	jwtAuthenticationFilter.setSuccessHandler((request, response, authentication) ->
		CsrfFilter.skipRequest(request));
	jwtAuthenticationFilter.setFailureHandler((request, response, exception) ->
		response.sendError(HttpServletResponse.SC_FORBIDDEN));

	PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
	authenticationProvider.setPreAuthenticatedUserDetailsService(new TokenAuthenticationUserDetailsService(this.inactiveTokenRepository));

	var refreshTokenFilter = new RefreshTokenFilter();
	refreshTokenFilter.setAccessTokenStringSerializer(this.accessTokenStringSerializer);

	JwtLogoutFilter jwtLogoutFilter = new JwtLogoutFilter(this.inactiveTokenRepository);

	builder.addFilterAfter(requestJwtTokensFilter, ExceptionTranslationFilter.class)
		.addFilterBefore(jwtAuthenticationFilter, CsrfFilter.class)
		.addFilterAfter(refreshTokenFilter, ExceptionTranslationFilter.class)
		.addFilterAfter(jwtLogoutFilter, ExceptionTranslationFilter.class)
		.authenticationProvider(authenticationProvider);
  }

  public JwtAuthenticationConfigurer refreshTokenStringSerializer(Function<Token, String> refreshTokenStringSerializer) {
	this.refreshTokenStringSerializer = refreshTokenStringSerializer;
	return this;
  }

  public JwtAuthenticationConfigurer accessTokenStringSerializer(Function<Token, String> accessTokenStringSerializer) {
	this.accessTokenStringSerializer = accessTokenStringSerializer;
	return this;
  }

  public JwtAuthenticationConfigurer accessTokenStringDeserializer(Function<String, Token> accessTokenStringDeserializer) {
	this.accessTokenStringDeserializer = accessTokenStringDeserializer;
	return this;
  }

  public JwtAuthenticationConfigurer refreshTokenStringDeserializer(Function<String, Token> refreshTokenStringDeserializer) {
	this.refreshTokenStringDeserializer = refreshTokenStringDeserializer;
	return this;
  }

  public JwtAuthenticationConfigurer inactiveTokenRepository(InactiveTokenRepository inactiveTokenRepository) {
	this.inactiveTokenRepository = inactiveTokenRepository;
	return this;
  }
}
