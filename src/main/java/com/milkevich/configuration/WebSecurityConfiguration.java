package com.milkevich.configuration;

import com.milkevich.repository.InactiveTokenRepository;
import com.milkevich.security.util.AccessTokenJwsStringDeserializer;
import com.milkevich.security.util.AccessTokenJwsStringSerializer;
import com.milkevich.security.JwtAuthenticationConfigurer;
import com.milkevich.security.util.RefreshTokenJweStringDeserializer;
import com.milkevich.security.util.RefreshTokenJweStringSerializer;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration {

  @Bean
  public JwtAuthenticationConfigurer jwtAuthenticationConfigurer(@Value("${jwt.access-token-key}") String accessTokenKey,
																 @Value("${jwt.refresh-token-key}") String refreshTokenKey,
																 InactiveTokenRepository inactiveTokenRepository
  ) throws ParseException, JOSEException {

	return new JwtAuthenticationConfigurer()
		.accessTokenStringSerializer(new AccessTokenJwsStringSerializer(
			new MACSigner(OctetSequenceKey.parse(accessTokenKey))
		))
		.refreshTokenStringSerializer(new RefreshTokenJweStringSerializer(
			new DirectEncrypter(OctetSequenceKey.parse(refreshTokenKey))
		))
		.accessTokenStringDeserializer(new AccessTokenJwsStringDeserializer(
			new MACVerifier(OctetSequenceKey.parse(accessTokenKey))
		))
		.refreshTokenStringDeserializer(new RefreshTokenJweStringDeserializer(
			new DirectDecrypter(OctetSequenceKey.parse(refreshTokenKey))
		))
		.inactiveTokenRepository(inactiveTokenRepository);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
												 JwtAuthenticationConfigurer jwtAuthenticationConfigurer) throws Exception {
	http.apply(jwtAuthenticationConfigurer);

	return http
		.httpBasic(Customizer.withDefaults())
		.sessionManagement(sessionManagement ->
			sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests(authorizeHttpRequests ->
			authorizeHttpRequests
				.requestMatchers("/api/**").hasRole("USER")
				.requestMatchers("/error").permitAll()
				.anyRequest().authenticated())
		.build();
  }
}