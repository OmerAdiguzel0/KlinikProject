package com.psychiatryclinic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Psikiyatri Kliniği API")
                        .version("1.0")
                        .description("Psikiyatri Kliniği Yönetim Sistemi için REST API")
                        .contact(new Contact()
                                .name("Psikiyatri Kliniği")
                                .url("https://psikiyatriklinigi.com")
                                .email("info@psikiyatriklinigi.com")));
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("Kullanıcı İşlemleri")
                .pathsToMatch("/api/v1/users/**")
                .build();
    }

    @Bean
    public GroupedOpenApi appointmentApi() {
        return GroupedOpenApi.builder()
                .group("Randevu İşlemleri")
                .pathsToMatch("/api/v1/appointments/**")
                .build();
    }

    @Bean
    public GroupedOpenApi invoiceApi() {
        return GroupedOpenApi.builder()
                .group("Fatura İşlemleri")
                .pathsToMatch("/api/v1/invoices/**")
                .build();
    }
} 