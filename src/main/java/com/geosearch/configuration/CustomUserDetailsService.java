package com.geosearch.configuration;

import com.geosearch.repository.RoleRepository;
import com.geosearch.repository.UserRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	return userRepository.findByUsername(username)
		.map(user -> User.builder()
			.username(user.getUsername())
			.password(user.getPassword())
			.authorities(roleRepository.findByUser_Id(user.getId())
				.stream()
				.map(role -> new SimpleGrantedAuthority(role.getRoleType().toString()))
				.collect(Collectors.toSet()))
			.build())
		.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
  }
}
