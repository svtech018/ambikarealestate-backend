package com.realestate.config.security;

import com.realestate.dto.ApiResponse;
import com.realestate.dto.LoginRequest;
import com.realestate.util.CookieUtil;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody LoginRequest request, 
                                                                   HttpServletRequest httpRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String token = jwtTokenProvider.generateToken(authentication);
        
        // Determine if production or development based on request host
        String host = httpRequest.getServerName();
        ResponseCookie cookie = CookieUtil.isProduction(host) 
                ? CookieUtil.createJwtCookie(token) 
                : CookieUtil.createJwtCookieDev(token);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        // Return only non-sensitive user info; token is set in httpOnly cookie
        return ResponseEntity.ok().headers(headers)
                .body(new ApiResponse<>(true, "Login successful", Map.of("username", request.getUsername())));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).body(new ApiResponse<>(false, "Not authenticated", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", Map.of("username", auth.getName())));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Map<String, String>>> logout(HttpServletRequest httpRequest) {
        // Determine if production or development based on request host
        String host = httpRequest.getServerName();
        ResponseCookie cookie = CookieUtil.isProduction(host)
                ? CookieUtil.deleteJwtCookie()
                : CookieUtil.deleteJwtCookieDev();
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().headers(headers).body(new ApiResponse<>(true, "Logged out", Map.of("status", "ok")));
    }
}
