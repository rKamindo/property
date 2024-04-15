package kamindo.propertymanager.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;
    private SecurityScheme createAPIKeyScheme() {
        final String securitySchemeName = "oauth2";

        return new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows()
                        .authorizationCode(new OAuthFlow()
                                .authorizationUrl(issuerUri + "authorize")
                                .tokenUrl(issuerUri + "oauth/token")
                                .scopes(new Scopes()
                                        .addString("openid", "OpenID Connect")
                                        .addString("profile", "Profile")
                                        .addString("email", "Email")))
                )
                .description("OAuth2 Authentication")
                .name(securitySchemeName);
    }

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "oauth2";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, createAPIKeyScheme()));
    }
}