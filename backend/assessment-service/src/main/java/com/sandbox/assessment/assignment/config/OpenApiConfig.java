package com.sandbox.assessment.assignment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI assignmentOpenAPI() {

        return new OpenAPI()

                .info(new Info()

                        .title("Assessment Assignment Service API")

                        .description("Member 4 Assignment Management APIs")

                        .version("1.0")

                        .contact(new Contact()

                                .name("Member 4")

                                .email("member4@sandbox.com")));
    }

}