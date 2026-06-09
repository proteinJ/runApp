package com.running.runapp.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Component
public class MonitoringBasicAuthFilter extends OncePerRequestFilter {

    private static final String PROMETHEUS_PATH = "/actuator/prometheus";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BASIC_PREFIX = "Basic ";

    @Value("${MONITORING_USERNAME:monitoring}")
    private String monitoringUsername;

    @Value("${MONITORING_PASSWORD:monitoring-local-password}")
    private String monitoringPassword;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !PROMETHEUS_PATH.equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (hasValidBasicAuth(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setHeader("WWW-Authenticate", "Basic realm=\"RunApp Monitoring\"");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private boolean hasValidBasicAuth(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization == null || !authorization.startsWith(BASIC_PREFIX)) {
            return false;
        }

        try {
            String encodedCredentials = authorization.substring(BASIC_PREFIX.length());
            String credentials = new String(Base64.getDecoder().decode(encodedCredentials), StandardCharsets.UTF_8);
            int separatorIndex = credentials.indexOf(':');
            if (separatorIndex < 0) {
                return false;
            }

            String username = credentials.substring(0, separatorIndex);
            String password = credentials.substring(separatorIndex + 1);
            return constantTimeEquals(username, monitoringUsername)
                    && constantTimeEquals(password, monitoringPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean constantTimeEquals(String actual, String expected) {
        return MessageDigest.isEqual(
                actual.getBytes(StandardCharsets.UTF_8),
                expected.getBytes(StandardCharsets.UTF_8)
        );
    }
}
