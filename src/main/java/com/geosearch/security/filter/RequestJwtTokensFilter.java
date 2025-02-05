package com.geosearch.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geosearch.security.factory.DefaultAccessTokenFactory;
import com.geosearch.security.factory.DefaultRefreshTokenFactory;
import com.geosearch.security.model.Token;
import com.geosearch.security.model.Tokens;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Objects;
import java.util.function.Function;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Setter
public class RequestJwtTokensFilter extends OncePerRequestFilter {

  private RequestMatcher requestMatcher = new AntPathRequestMatcher("/jwt/tokens", HttpMethod.POST.name());

  private SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

  private Function<Authentication, Token> refreshTokenFactory = new DefaultRefreshTokenFactory();
  private Function<Token, Token> accessTokenFactory = new DefaultAccessTokenFactory();

  private Function<Token, String> refreshTokenStringSerializer = Objects::toString;
  private Function<Token, String> accessTokenStringSerializer = Objects::toString;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
								  FilterChain filterChain) throws ServletException, IOException {

	if (this.requestMatcher.matches(request)) {
	  if (this.securityContextRepository.containsContext(request)) {
		SecurityContext contextRepository = this.securityContextRepository.loadDeferredContext(request).get();
		if (contextRepository != null && !(contextRepository.getAuthentication() instanceof PreAuthenticatedAuthenticationToken)) {
		  Token refreshToken = this.refreshTokenFactory.apply(contextRepository.getAuthentication());
		  Token accessToken = this.accessTokenFactory.apply(refreshToken);

		  response.setStatus(HttpServletResponse.SC_OK);
		  response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		  this.objectMapper.writeValue(response.getWriter(),
			  new Tokens(this.accessTokenStringSerializer.apply(accessToken),
				  accessToken.expiresAt().toString(),
				  this.refreshTokenStringSerializer.apply(refreshToken),
				  refreshToken.expiresAt().toString()));
		  return;
		}
	  }
	  throw new AccessDeniedException("Пользователь должен быть аутентифицирован");
	}

	filterChain.doFilter(request, response);

  }
}
