package com.socialcircle.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;

@Component
public class CustomAccessExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(new ObjectMapper().createObjectNode()
                .put("timestamp", ZonedDateTime.now().toString())
                .put("message", "Access denied")
                .put("uri", request.getRequestURI())
                .put("status", response.getStatus())
                .put("content-type", response.getContentType())
                .put("from", "Vanilla Auth System")
                .toString());
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(new ObjectMapper().createObjectNode()
                .put("timestamp", ZonedDateTime.now().toString())
                .put("message", "Access denied")
                .put("uri", request.getRequestURI())
                .put("status", response.getStatus())
                .put("content-type", response.getContentType())
                .put("from", "socialcircle Auth System")
                .toString());
    }

}
