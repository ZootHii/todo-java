package com.zoothii.iwbtodojava.core.utulities.security.token;

import org.springframework.security.core.Authentication;

public interface TokenUtils {
    AccessToken createAccessToken(Authentication authentication);

    String getUserNameFromAccessToken(String accessToken);

    boolean validateAccessToken(String accessToken);
}
