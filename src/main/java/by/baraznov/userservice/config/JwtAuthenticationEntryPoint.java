package by.baraznov.userservice.config;


import by.baraznov.userservice.utils.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;
    private static final String MESSAGE = "Access token is missing or invalid";
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        sendErrorResponse(response);
    }
    private void sendErrorResponse(ServletResponse response) throws IOException {
        if(response instanceof HttpServletResponse httpServletResponse) {
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorResponse dto = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), JwtAuthenticationEntryPoint.MESSAGE, LocalDateTime.now());
            String json = objectMapper.writeValueAsString(dto);
            httpServletResponse.getWriter().write(json);
        }
    }
}
