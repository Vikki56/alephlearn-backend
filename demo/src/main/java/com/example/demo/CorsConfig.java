// package com.example.demo;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.web.filter.CorsFilter;

// import java.util.List;

// @Configuration
// public class CorsConfig {

//     @Bean
//     public CorsFilter corsFilter() {
//         CorsConfiguration config = new CorsConfiguration();
        
//         // ✅ DO NOT use allowedOrigins("*") when allowCredentials = true
//         // use allowedOriginPatterns instead:
//         config.setAllowedOriginPatterns(List.of(
//             "http://localhost:*",      // local dev ports (e.g. 8080, 5173, 3000)
//             "http://127.0.0.1:*",      // loopback
//             "null"                     // for file:// origins (some browsers send Origin:null)
//         ));

//         config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
//         config.setAllowedHeaders(List.of("*"));
//         config.setAllowCredentials(true);
//         config.setMaxAge(3600L);

//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", config);

//         return new CorsFilter(source);
//     }
// }
