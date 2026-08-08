package com.sandbox.assessment.config;

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

import com.sandbox.assessment.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

            /*
             * Allow React frontend running on :5173
             * to communicate with Assessment Service.
             */
            .cors(cors ->
                cors.configurationSource(corsConfigurationSource())
            )

            /*
             * REST API + JWT.
             *
             * We are not using browser sessions/forms.
             */
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                /*
                 * ============================================================
                 * CORS PREFLIGHT
                 * ============================================================
                 */

                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                ).permitAll()


                /*
                 * ============================================================
                 * HR OPERATIONS
                 * ============================================================
                 *
                 * Only HR can create/publish assessments
                 * and create questions.
                 */

                .requestMatchers(
                    HttpMethod.POST,
                    "/assessment/create"
                ).hasRole("HR")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/assessment/*/publish"
                ).hasRole("HR")

                .requestMatchers(
                    HttpMethod.POST,
                    "/question/create"
                ).hasRole("HR")


                /*
                 * ============================================================
                 * CANDIDATE ASSESSMENT ACCESS
                 * ============================================================
                 *
                 * Candidate dashboard needs to read assignments.
                 */

                .requestMatchers(
                    HttpMethod.GET,
                    "/assessment/assignments/candidate/**"
                ).authenticated()


                /*
                 * ============================================================
                 * GENERAL READ OPERATIONS
                 * ============================================================
                 *
                 * Any authenticated user can read assessment/question data.
                 *
                 * More restrictive rules can be added later when required.
                 */

                .requestMatchers(
                    HttpMethod.GET,
                    "/assessment/**"
                ).authenticated()

                .requestMatchers(
                    HttpMethod.GET,
                    "/question/**"
                ).authenticated()


                /*
                 * ============================================================
                 * CANDIDATE SUBMISSION OPERATIONS
                 * ============================================================
                 */

                .requestMatchers(
                    HttpMethod.POST,
                    "/assessment-submission/start/**"
                ).hasRole("CANDIDATE")

                .requestMatchers(
                    HttpMethod.POST,
                    "/assessment-submission/finish/**"
                ).hasRole("CANDIDATE")

                .requestMatchers(
                    HttpMethod.POST,
                    "/candidate-answer/coding"
                ).hasRole("CANDIDATE")

                .requestMatchers(
                    HttpMethod.POST,
                    "/candidate-answer/mcq"
                ).hasRole("CANDIDATE")


                /*
                 * ============================================================
                 * EVERYTHING ELSE
                 * ============================================================
                 *
                 * Nothing is publicly accessible unless explicitly
                 * permitted above.
                 */

                .anyRequest().authenticated()
            )


            /*
             * Validate JWT before Spring's normal
             * username/password authentication filter.
             */
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }


    /*
     * ================================================================
     * CORS CONFIGURATION
     * ================================================================
     */

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
            new CorsConfiguration();

        /*
         * React development server
         */
        configuration.setAllowedOrigins(
            List.of("http://localhost:5173")
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
            List.of("Authorization")
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