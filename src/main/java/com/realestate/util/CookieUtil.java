package com.realestate.util;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Utility class for creating secure JWT cookies that work across subdomains.
 * 
 * Key Settings:
 * - httpOnly(true): Prevents XSS attacks by making cookie inaccessible to JavaScript
 * - secure(true): Only sent over HTTPS (required for production)
 * - sameSite("None"): Allows cookie to be sent across different subdomains/origins
 * - domain("ambikarealestate.com"): Applies to all subdomains (api., www., etc.)
 */
@Component
public class CookieUtil {

    private static final String COOKIE_NAME = "jwt";
    private static final String COOKIE_DOMAIN = "ambikarealestate.com"; // Domain without 'www' for subdomain compatibility
    private static final int COOKIE_MAX_AGE_DAYS = 7;

    /**
     * Create a secure JWT cookie for production/staging environments.
     * 
     * @param jwtToken The JWT token string
     * @return ResponseCookie with secure settings for HTTPS
     */
    public static ResponseCookie createJwtCookie(String jwtToken) {
        return ResponseCookie
                .from(COOKIE_NAME, jwtToken)
                .httpOnly(true)                                    // ✅ XSS Protection: Not accessible via JavaScript
                .secure(true)                                      // ✅ Only sent over HTTPS
                .path("/")                                         // Available to all paths
                .maxAge(Duration.ofDays(COOKIE_MAX_AGE_DAYS))     // Expires in 7 days
                .domain(COOKIE_DOMAIN)                             // ✅ Works across subdomains: api.ambikarealestate.com → ambikarealestate.com
                .sameSite("None")                                  // ✅ Cross-site cookie: Required for same-domain API calls
                .build();
    }

    /**
     * Create a JWT cookie for local development (localhost).
     * Note: SameSite="Lax" on localhost, secure=false because HTTP only
     * 
     * @param jwtToken The JWT token string
     * @return ResponseCookie for development
     */
    public static ResponseCookie createJwtCookieDev(String jwtToken) {
        return ResponseCookie
                .from(COOKIE_NAME, jwtToken)
                .httpOnly(true)
                .secure(false)                                     // ❌ HTTP only for local dev
                .path("/")
                .maxAge(Duration.ofDays(COOKIE_MAX_AGE_DAYS))
                .sameSite("Lax")                                   // Lax for localhost (more permissive)
                .build();
    }

    /**
     * Create a cookie to clear/delete the JWT (logout).
     * 
     * @return ResponseCookie with maxAge=0 to delete
     */
    public static ResponseCookie deleteJwtCookie() {
        return ResponseCookie
                .from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)                                         // ✅ Immediate deletion
                .domain(COOKIE_DOMAIN)
                .sameSite("None")
                .build();
    }

    /**
     * Create a logout cookie for development.
     * 
     * @return ResponseCookie for dev logout
     */
    public static ResponseCookie deleteJwtCookieDev() {
        return ResponseCookie
                .from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }

    /**
     * Determine if running in production based on environment.
     * 
     * @return true if production (HTTPS domains), false if development
     */
    public static boolean isProduction(String host) {
        return host != null && (host.contains("ambikarealestate.com") || host.contains("api.ambikarealestate.com"));
    }
}
