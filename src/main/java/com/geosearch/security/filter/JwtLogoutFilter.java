package com.geosearch.security.filter;

import com.geosearch.entity.InactiveToken;
import com.geosearch.repository.InactiveTokenRepository;
import com.geosearch.security.model.TokenUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtLogoutFilter extends OncePerRequestFilter {

  private RequestMatcher requestMatcher = new AntPathRequestMatcher("/jwt/logout", HttpMethod.POST.name());

  private SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();
  private final InactiveTokenRepository inactiveTokenRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
	if (this.requestMatcher.matches(request)) {
	  if (this.securityContextRepository.containsContext(request)) {
		SecurityContext context = this.securityContextRepository.loadDeferredContext(request).get();
		if (context != null && context.getAuthentication() instanceof PreAuthenticatedAuthenticationToken &&
			context.getAuthentication().getPrincipal() instanceof TokenUser user &&
			context.getAuthentication().getAuthorities()
				.contains(new SimpleGrantedAuthority("JWT_LOGOUT"))) {

		  LocalDateTime localDateTime = LocalDateTime.ofInstant(user.getToken().expiresAt(), ZoneId.systemDefault());
		  inactiveTokenRepository.save(new InactiveToken(localDateTime));

		  response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		  return;
		}
	  }
	  throw new AccessDeniedException("Пользователь должен быть аутентифицирован с помощью JWT");
	}
	filterChain.doFilter(request, response);
  }
}