package com.porlio.porliobe.module.iam.auth.service;

import com.porlio.porliobe.module.iam.auth.dto.request.AuthenticationRequest;
import com.porlio.porliobe.module.iam.session.dto.response.SessionResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {

  SessionResponse authenticate(AuthenticationRequest request, HttpServletResponse response);
}
