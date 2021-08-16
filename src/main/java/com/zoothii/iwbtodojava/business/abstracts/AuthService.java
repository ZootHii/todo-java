package com.zoothii.iwbtodojava.business.abstracts;

import com.zoothii.iwbtodojava.core.entities.User;
import com.zoothii.iwbtodojava.core.utulities.results.DataResult;
import com.zoothii.iwbtodojava.core.utulities.results.Result;
import com.zoothii.iwbtodojava.entities.concretes.payload.request.LoginRequest;
import com.zoothii.iwbtodojava.entities.concretes.payload.request.RegisterRequest;
import com.zoothii.iwbtodojava.entities.concretes.payload.response.AuthResponse;

public interface AuthService {
    DataResult<AuthResponse> register(RegisterRequest registerRequest);

    DataResult<AuthResponse> login(LoginRequest loginRequest);

    Result checkIfPasswordCorrect(String requestedPassword, String encryptedPassword);

    DataResult<User> getAuthenticatedUserDetails();
}
