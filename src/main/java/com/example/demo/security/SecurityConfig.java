package com.example.demo.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // ---- CORS config for local dev + Swagger + frontend ----
        CorsConfiguration corsCfg = new CorsConfiguration();
        corsCfg.setAllowedOriginPatterns(List.of(
            "http://localhost:*",
            "http://127.0.0.1:*",
            "null",
    
            // ---- PROD / HOSTED FRONTEND ----
            "https://alephlearn.com",
            "https://www.alephlearn.com",
            "https://app.alephlearn.com",
    
            // Cloudflare Pages default domains (agar use ho)
            "https://*.pages.dev"
    ));
        corsCfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
        corsCfg.setAllowedHeaders(List.of("*"));
        corsCfg.setAllowCredentials(true);

        http
            .cors(cors -> cors.configurationSource(req -> corsCfg))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // -------------------- STATIC --------------------
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                // -------------------- PUBLIC GET (uploads/images/papers etc.) --------------------
                .requestMatchers(HttpMethod.GET,
                        "/uploads/**",
                        "/images/**",
                        "/api/questions/*/image",
                        "/api/answers/*/image",
                        "/api/questions/*/am-i-claimer",
                        "/api/papers/**"
                ).permitAll()

                // -------------------- SWAGGER / WS / AUTH / PING / ERROR / H2 --------------------
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/ws/**",
                        "/ws/notify/**",
                        "/api/ping",
                        "/api/auth/**",
                        "/h2-console/**",
                        "/error"
                ).permitAll()
                .requestMatchers("/api/public/**").permitAll()

                // Allow CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public uploads (if you really want these public)
                .requestMatchers(HttpMethod.POST, "/api/messages/upload-audio").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/files/upload").permitAll()

                // -------------------- QUIZZES (public endpoints) --------------------
                .requestMatchers("/api/quizzes/public", "/api/quizzes/code/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/quizzes/*/status").permitAll()

                // -------------------- ADMIN ONLY --------------------
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // -------------------- QUIZZES (protected) --------------------
                // USER/TEACHER/ADMIN all can access other quiz APIs
                .requestMatchers("/api/quizzes/**").hasAnyRole("USER", "TEACHER", "ADMIN")
// Public bug reports from landing page (no login)
.requestMatchers(HttpMethod.POST, "/api/reports/bug").permitAll()
                // -------------------- USER + ADMIN (Teacher blocked from these) --------------------
                .requestMatchers("/api/profile/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/dashboard/**").hasAnyRole("USER", "ADMIN")

                .requestMatchers("/api/chat/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/doubts/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/doubt-rooms/**").hasAnyRole("USER", "ADMIN")

                // Reports (USER can report, ADMIN can review)
                .requestMatchers("/api/reports/**").hasAnyRole("USER", "ADMIN")

                // AI explain (keep teacher out)
                .requestMatchers("/api/aiqa/**").hasAnyRole("USER", "ADMIN")

                // -------------------- EVERYTHING ELSE --------------------
                .anyRequest().authenticated()
            )
            // H2 console & iframes etc.
            .headers(h -> h.frameOptions(f -> f.disable()));

        // JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}