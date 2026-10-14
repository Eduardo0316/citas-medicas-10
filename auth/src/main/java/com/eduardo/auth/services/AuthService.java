package com.eduardo.auth.services;

import com.eduardo.auth.dto.LoginRequest;
import com.eduardo.auth.dto.TokenResponse;

public interface AuthService {

    TokenResponse autenticar(LoginRequest request) throws Exception;
}