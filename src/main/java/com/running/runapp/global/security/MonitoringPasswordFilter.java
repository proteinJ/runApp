package com.running.runapp.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Component
public class MonitoringPasswordFilter extends OncePerRequestFilter {

    private static final String PROMETHEUS_PATH = "/actuator/prometheus";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String PASSWORD_PARAMETER = "password";
    private static final String COOKIE_NAME = "runapp_monitoring";

    @Value("${MONITORING_PASSWORD:1234}")
    private String monitoringPassword;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !PROMETHEUS_PATH.equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (hasValidPasswordHeader(request) || hasValidPasswordCookie(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            handlePasswordSubmit(request, response);
            return;
        }

        renderPasswordPage(response, false);
    }

    private void handlePasswordSubmit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String password = request.getParameter(PASSWORD_PARAMETER);
        if (constantTimeEquals(password, monitoringPassword)) {
            response.addHeader("Set-Cookie", COOKIE_NAME + "=" + passwordHash()
                    + "; Path=" + PROMETHEUS_PATH
                    + "; HttpOnly; SameSite=Lax; Max-Age=3600");
            response.sendRedirect(PROMETHEUS_PATH);
            return;
        }

        renderPasswordPage(response, true);
    }

    private boolean hasValidPasswordHeader(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            return false;
        }
        return constantTimeEquals(authorization.substring(BEARER_PREFIX.length()), monitoringPassword);
    }

    private boolean hasValidPasswordCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }

        String expectedHash = passwordHash();
        return Arrays.stream(cookies)
                .filter(cookie -> COOKIE_NAME.equals(cookie.getName()))
                .anyMatch(cookie -> constantTimeEquals(cookie.getValue(), expectedHash));
    }

    private void renderPasswordPage(HttpServletResponse response, boolean hasError) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("""
                <!doctype html>
                <html lang="ko">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>RunApp Monitoring</title>
                  <style>
                    body { margin: 0; min-height: 100vh; display: grid; place-items: center; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; background: #111827; color: #f9fafb; }
                    main { width: min(360px, calc(100vw - 40px)); }
                    h1 { margin: 0 0 18px; font-size: 22px; font-weight: 700; }
                    form { display: grid; gap: 12px; }
                    input { height: 44px; border: 1px solid #374151; border-radius: 6px; padding: 0 12px; background: #030712; color: #f9fafb; font-size: 18px; letter-spacing: 0; }
                    button { height: 44px; border: 0; border-radius: 6px; background: #2563eb; color: white; font-size: 15px; font-weight: 700; cursor: pointer; }
                    p { min-height: 20px; margin: 12px 0 0; color: #f87171; font-size: 14px; }
                  </style>
                </head>
                <body>
                  <main>
                    <h1>RunApp Monitoring</h1>
                    <form method="post" action="/actuator/prometheus">
                      <input name="password" type="password" inputmode="numeric" pattern="[0-9]{4}" maxlength="4" autocomplete="current-password" placeholder="4자리 비밀번호" autofocus>
                      <button type="submit">확인</button>
                    </form>
                    <p>%s</p>
                  </main>
                </body>
                </html>
                """.formatted(hasError ? "비밀번호가 올바르지 않습니다." : ""));
    }

    private String passwordHash() {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(monitoringPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(digest.length * 2);
            for (byte value : digest) {
                builder.append(String.format("%02x", value));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    private boolean constantTimeEquals(String actual, String expected) {
        if (actual == null || expected == null) {
            return false;
        }
        return MessageDigest.isEqual(
                actual.getBytes(StandardCharsets.UTF_8),
                expected.getBytes(StandardCharsets.UTF_8)
        );
    }
}
