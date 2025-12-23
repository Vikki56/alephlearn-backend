package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import static org.mockito.Mockito.mock;

@SpringBootTest
@Import(DemoApplicationTests.MockMailConfig.class)
class DemoApplicationTests {

    @Test
    void contextLoads() {
        // just verifies the Spring context starts
    }

    static class MockMailConfig {
        @Bean
        JavaMailSender mailSender() {
            // prevent real SMTP during tests
            return mock(JavaMailSender.class);
        }
    }
}