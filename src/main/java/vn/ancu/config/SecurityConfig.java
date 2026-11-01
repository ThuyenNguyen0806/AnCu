package vn.ancu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {
    // Dùng cùng một encoder để tạo mật khẩu và kiểm tra lúc đăng nhập.
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService users(@Value("${ancu.admin.username}") String username,
            @Value("${ancu.admin.password}") String password,
            PasswordEncoder passwordEncoder) {
        if (password.isBlank()) throw new IllegalArgumentException("Cần cấu hình ADMIN_PASSWORD");
        return new InMemoryUserDetailsManager(User.withUsername(username)
                .password(passwordEncoder.encode(password)).roles("ADMIN").build());
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/style.css", "/app.js", "/images/**", "/login", "/error", "/health").permitAll()
                    .anyRequest().authenticated())
                .formLogin(login -> login.loginPage("/login")
                    .defaultSuccessUrl("/housing/dashboard", true).permitAll())
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
        return http.build();
    }
}
