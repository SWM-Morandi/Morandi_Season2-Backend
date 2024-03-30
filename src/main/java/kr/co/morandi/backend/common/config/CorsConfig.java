package kr.co.morandi.backend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080",
                "http://morandi.co.kr",
                "https://morandi.co.kr",
                "http://api.morandi.co.kr",
                "https://api.morandi.co.kr",
                "chrome-extension://ljkmahbkojffhjdjkghaljooajocajnf",
                "chrome-extension://cmblaiddbfchipealeopkbbnboifeedc",
                "chrome-extension://ckepgfjakcdkjpabldbamcfcjhcdojih",
                "chrome-extension://lfhcfibdigjcendabogbkpcofcgkhfep"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
