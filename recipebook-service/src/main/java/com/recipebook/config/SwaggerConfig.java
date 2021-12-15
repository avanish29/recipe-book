package com.recipebook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author - AvanishKishorPandey
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.recipebook.web.rest"))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .pathMapping("/");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Recipe - Recipe Service")
                .description("This API reference is organized by resource type Recipe. Each resource type has one or more endpoint. " +
                        "To use these endpoints, a token needs to be issued by the application by using /sign-in call with username and password." +
                        "This API Specification describes how to use it in other systems.")
                .contact(new Contact("Support", "http://localhost:8080/", "avanish.k.pandey@gmail.com"))
                .build();
    }
}