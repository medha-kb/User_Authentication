package com.intuit.securityconfig;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		// the commence method is responsible for logging authentication errors and
		// sending an HTTP response with an "unauthorized" status code and a custom
		// error message when an unauthenticated or unauthorized user tries to access a
		// protected resource.
		System.out.println("Unautherized Error: {} " + authException.getMessage());
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getOutputStream().print("{\"error\":\"Unauthorized.. Please authenticate..\"}");
		response.setContentType("application/json;charset=UTF-8");

		// try (PrintWriter writer = response.getWriter()) {
		// // Here, you can use a JSON library to convert your error object to JSON, or
		// you
		// // can manually create a JSON string.
		// String jsonError = "{\"error\":\"Unauthorized\",\"message\":\"" +
		// authException.getMessage() + "\"}";
		// writer.write(jsonError);
		// }
	}

}
