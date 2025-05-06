package com.milkevich.security;

import com.milkevich.repository.InactiveTokenRepository;
import com.milkevich.security.filter.JwtLogoutFilter;
import com.milkevich.security.filter.RefreshTokenFilter;
import com.milkevich.security.filter.RequestJwtTokensFilter;
import com.milkevich.security.model.AccessToken;
import com.milkevich.security.model.RefreshToken;
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

  private Function<RefreshToken, String> refreshTokenStringSerializer = Objects::toString;
  private Function<AccessToken, String> accessTokenStringSerializer = Objects::toString;

  private Function<String, AccessToken> accessTokenStringDeserializer;
  private Function<String, RefreshToken> refreshTokenStringDeserializer;

  private InactiveTokenRepository inactiveTokenRepository;

  @Override
  public void init(HttpSecurity builder) throws Exception {
	var csrfConfigurer = builder.getConfigurer(CsrfConfigurer.class);
	if (csrfConfigurer != null) {
	  csrfConfigurer.ignoringRequestMatchers(new AntPathRequestMatcher("/jwt/tokens", "POST"));
	}
  }

  @Override
  public void configure(HttpSecurity builder) {
	RequestJwtTokensFilter requestJwtTokensFilter = new RequestJwtTokensFilter();
	requestJwtTokensFilter.setAccessTokenStringSerializer(this.accessTokenStringSerializer);
	requestJwtTokensFilter.setRefreshTokenStringSerializer(this.refreshTokenStringSerializer);

	AuthenticationFilter jwtAuthenticationFilter = new AuthenticationFilter(builder.getSharedObject(AuthenticationManager.class),
		new JwtAuthenticationConverter(this.accessTokenStringDeserializer, this.refreshTokenStringDeserializer));
	jwtAuthenticationFilter.setSuccessHandler((request, response, authentication) ->
		CsrfFilter.skipRequest(request));
	jwtAuthenticationFilter.setFailureHandler((request, response, exception) ->
		response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: "));

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

  public JwtAuthenticationConfigurer refreshTokenStringSerializer(Function<RefreshToken, String> refreshTokenStringSerializer) {
	this.refreshTokenStringSerializer = refreshTokenStringSerializer;
	return this;
  }

  public JwtAuthenticationConfigurer accessTokenStringSerializer(Function<AccessToken, String> accessTokenStringSerializer) {
	this.accessTokenStringSerializer = accessTokenStringSerializer;
	return this;
  }

  public JwtAuthenticationConfigurer accessTokenStringDeserializer(Function<String, AccessToken> accessTokenStringDeserializer) {
	this.accessTokenStringDeserializer = accessTokenStringDeserializer;
	return this;
  }

  public JwtAuthenticationConfigurer refreshTokenStringDeserializer(Function<String, RefreshToken> refreshTokenStringDeserializer) {
	this.refreshTokenStringDeserializer = refreshTokenStringDeserializer;
	return this;
  }

  public JwtAuthenticationConfigurer inactiveTokenRepository(InactiveTokenRepository inactiveTokenRepository) {
	this.inactiveTokenRepository = inactiveTokenRepository;
	return this;
  }
}
