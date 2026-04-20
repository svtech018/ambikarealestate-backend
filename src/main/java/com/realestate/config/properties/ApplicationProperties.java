package com.realestate.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

    private Property property = new Property();
    private WhatsApp whatsapp = new WhatsApp();
    private Maps maps = new Maps();
    private Youtube youtube = new Youtube();

    @Data
    public static class Property {
        private Images images = new Images();

        @Data
        public static class Images {
            private String uploadDir = "./uploads/property-images";
            private int maxFiles = 10;
            private String allowedExtensions = "jpg,jpeg,png,webp";
        }
    }

    @Data
    public static class WhatsApp {
        private String accountSid;
        private String authToken;
        private String fromNumber;
    }

    @Data
    public static class Maps {
        private String apiKey;
    }

    @Data
    public static class Youtube {
        private String apiKey;
    }
}