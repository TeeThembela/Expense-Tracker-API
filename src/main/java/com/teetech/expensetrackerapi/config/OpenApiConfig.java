package com.teetech.expensetrackerapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI expenseTrackerOpenAPI(){
        final String securitySchemeName = "Bearer";

        return new OpenAPI()
                .info(
                        new Info()
                            .title("Expense Tracker API")
                            .description("""
                                A secure REST API for tracking personal expenses, managing budgets, \
                                and organising spending categories.
 
                                ## Authentication
                                Most endpoints require a valid **JWT access token** supplied as a \
                                `Bearer` token in the `Authorization` header.
                                Obtain a token via `POST /api/v1/auth/login`, then click the \
                                **Authorize** button above and paste the token value.
 
                                **Access tokens** are short-lived and carried in the Authorization header.
                                **Refresh tokens** are long-lived, stored in an `HttpOnly` cookie, \
                                and used exclusively at `POST /api/v1/auth/refresh`.
 
                                ## Authorisation
                                Resource endpoints enforce **owner-level isolation** — a user may \
                                only access their own data.
                                Admin endpoints under `/api/v1/admin/**` require the `ROLE_MANAGER` authority.
                                """)
                            .version("v1.0.0"))
                    // Applies JWT Bearer globally to every endpoint.
                    // Public endpoints (register, login, refresh) override this with security = {}
                    // in their @Operation annotation.
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description(
                                                "JWT access token obtained from POST /api/v1/auth/login. " +
                                                "Paste the token value only — the 'Bearer ' prefix " +
                                                "is added automatically by Swagger UI."
                                        )));
    }
}
