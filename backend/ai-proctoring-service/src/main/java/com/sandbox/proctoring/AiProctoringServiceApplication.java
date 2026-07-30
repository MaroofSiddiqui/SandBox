package com.sandbox.proctoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// exclude all sql, jpa, and devtools datasource configs so spring boot uses mongodb only
@SpringBootApplication(excludeName = {
    "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
    "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
    "org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration",
    "org.springframework.boot.devtools.autoconfigure.DevToolsDataSourceAutoConfiguration"
})
public class AiProctoringServiceApplication {

    public static void main(String[] args) {
        // starts microservice on port 8083
        SpringApplication.run(AiProctoringServiceApplication.class, args);
    }
}