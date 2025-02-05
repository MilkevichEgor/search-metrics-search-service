package com.geosearch.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geosearch.security.factory.DefaultAccessTokenFactory;
import com.geosearch.security.model.AccessToken;
import com.geosearch.security.model.RefreshToken;
import com.geosearch.security.model.RefreshTokenUser;
import com.geosearch.security.model.Tokens;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Function;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Setter
public class RefreshTokenFilter extends OncePerRequestFilter {

  private RequestMatcher requestMatcher = new AntPathRequestMatcher("/jwt/refresh", HttpMethod.POST.name());

  private SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

  private Function<RefreshToken, AccessToken> accessTokenFactory = new DefaultAccessTokenFactory();

  private Function<AccessToken, String> accessTokenStringSerializer = Object::toString;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

	if (this.requestMatcher.matches(request)) {
	  if (this.securityContextRepository.containsContext(request)) {
		SecurityContext context = this.securityContextRepository.loadDeferredContext(request).get();
		if (context != null && context.getAuthentication() instanceof PreAuthenticatedAuthenticationToken &&
			context.getAuthentication().getPrincipal() instanceof RefreshTokenUser user &&
			context.getAuthentication().getAuthorities()
				.contains(new SimpleGrantedAuthority("JWT_REFRESH"))) {
		  var accessToken = this.accessTokenFactory.apply(user.getRefreshToken());

		  response.setStatus(HttpServletResponse.SC_OK);
		  response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		  this.objectMapper.writeValue(response.getWriter(),
			  new Tokens(this.accessTokenStringSerializer.apply(accessToken),
				  accessToken.expiresAt().toString(), null, null));
		  return;
		}
	  }

	  throw new AccessDeniedException("Пользователь должен быть аутентифицирован с помощью JWT");
	}

	filterChain.doFilter(request, response);
  }
}
