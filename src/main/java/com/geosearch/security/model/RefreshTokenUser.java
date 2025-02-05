package com.geosearch.security.model;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class RefreshTokenUser extends User {

  private final RefreshToken refreshToken;

  public RefreshTokenUser(String username, String password, Collection<? extends GrantedAuthority> authorities, RefreshToken refreshToken) {
	super(username, password, authorities);
	this.refreshToken = refreshToken;
  }

  public RefreshTokenUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, RefreshToken refreshToken) {
	super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	this.refreshToken = refreshToken;
  }
}
