package com.realestate.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();

    @Data
    public static class Jwt {
        private String secretKey = "myDefaultSecretKeyThatIsLongEnoughForHS256Algorithm1234567890";
        private long expirationTime = 86400000; // 24 hours
        private long refreshExpirationTime = 604800000; // 7 days
    }

    @Data
    public static class Cors {
        private String allowedOrigins = "http://localhost:3000,http://localhost:4200";
        private String allowedMethods = "GET,POST,PUT,DELETE,OPTIONS";
        private String allowedHeaders = "*";
        private boolean allowCredentials = true;
    }
}