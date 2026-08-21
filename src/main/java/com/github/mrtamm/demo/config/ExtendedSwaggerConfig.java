package com.github.mrtamm.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Minimal config for the Swagger documentation.
 * Note that some settings are also in application.yml.
 */
@Configuration
public class ExtendedSwaggerConfig implements WebMvcConfigurer {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("User Management Demo API")
            .version("1.0")
            .description("Demonstrates simple user management with \"list\", \"add\", and " +
                "\"update\" operations.")
        );
  }

}
