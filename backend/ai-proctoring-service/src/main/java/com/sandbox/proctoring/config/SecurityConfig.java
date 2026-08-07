package com.sandbox.proctoring.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.sandbox.proctoring.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            .cors(cors ->
                cors.configurationSource(corsConfigurationSource())
            )

            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                /*
                 * Browser CORS preflight requests.
                 */
                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                )
                .permitAll()


                /*
                 * =====================================
                 * CANDIDATE OPERATIONS
                 * =====================================
                 */

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/evaluations/run"
                )
                .hasRole("CANDIDATE")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/evaluations/submit-and-save"
                )
                .hasRole("CANDIDATE")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/proctoring/validate-device"
                )
                .hasRole("CANDIDATE")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/proctoring/log-violation"
                )
                .hasRole("CANDIDATE")

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/proctoring/upload-evidence"
                )
                .hasRole("CANDIDATE")


                /*
                 * =====================================
                 * HR OPERATIONS
                 * =====================================
                 */

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/evaluations"
                )
                .hasRole("HR")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/evaluations/student/**"
                )
                .hasRole("HR")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/evaluations/*"
                )
                .hasRole("HR")

                /*
                 * Everything else in this service
                 * requires a valid JWT.
                 */
                .anyRequest()
                .authenticated()
            )

            /*
             * Read JWT before Spring's normal
             * username/password authentication filter.
             */
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        /*
         * React/Vite frontend.
         */
        configuration.setAllowedOrigins(
            List.of(
                "http://localhost:5173"
            )
        );

        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(
            List.of(
                "Authorization",
                "Content-Type"
            )
        );

        configuration.setExposedHeaders(
            List.of(
                "Authorization"
            )
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }
}