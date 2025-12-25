package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebCorsConfig implements WebMvcConfigurer {
  @Override 
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")  
        .allowedOriginPatterns(
            "http://localhost:*", 
            "http://127.0.0.1:*", 
            "null")             
        .allowedMethods("GET","POST","PATCH","DELETE","OPTIONS","HEAD")
        .allowedHeaders("*")
        .allowCredentials(true) 
        .maxAge(3600);
  }
}
