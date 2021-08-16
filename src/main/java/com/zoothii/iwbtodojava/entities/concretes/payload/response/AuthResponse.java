package com.zoothii.iwbtodojava.entities.concretes.payload.response;

import com.zoothii.iwbtodojava.core.utulities.security.token.AccessToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
	private AccessToken accessToken;
	private Long id;
	private String username;
	private String email;
	private List<String> roles;
}
