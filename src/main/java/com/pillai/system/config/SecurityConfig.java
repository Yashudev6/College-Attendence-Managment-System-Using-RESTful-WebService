package com.pillai.system.config;

import com.pillai.system.security.JwtAuthenticationFilter;
import com.pillai.system.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json");
                    response.setStatus(401);
                    response.getWriter().write("{\"timestamp\":\"" + java.time.LocalDateTime.now() + "\",\"status\":401,\"message\":\"Unauthorized access: Please provide a valid Bearer token\",\"path\":\"" + request.getRequestURI() + "\"}");
                })
            )
            .authorizeHttpRequests(auth -> auth
                // Public auth endpoints
                .requestMatchers("/api/auth/**").permitAll()
                
                // Static Web UI resources
                .requestMatchers("/", "/index.html", "/admin.html", "/teacher.html", "/student.html").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/assets/**", "/favicon.ico").permitAll()
                
                // Admin specific endpoints
                .requestMatchers(HttpMethod.POST, "/api/departments/**", "/api/classes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/departments/**", "/api/classes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/departments/**", "/api/classes/**", "/api/students/**", "/api/teachers/**", "/api/subjects/**").hasRole("ADMIN")
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                
                // Teacher / Admin endpoints for Attendance & Results
                .requestMatchers(HttpMethod.POST, "/api/attendance/**", "/api/results/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.PUT, "/api/attendance/**", "/api/results/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.PATCH, "/api/attendance/**", "/api/results/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.DELETE, "/api/attendance/**", "/api/results/**").hasAnyRole("ADMIN", "TEACHER")
                
                // Authenticated access for GET endpoints (Students can view data, Teachers/Admins can view all)
                .requestMatchers(HttpMethod.GET, "/api/students/**", "/api/teachers/**", "/api/departments/**", "/api/classes/**", "/api/subjects/**", "/api/attendance/**", "/api/results/**", "/api/dashboard/**").authenticated()
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
