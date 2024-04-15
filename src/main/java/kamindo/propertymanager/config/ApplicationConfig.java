package kamindo.propertymanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfig {

    @Value("${OKTA_ISSUER}")
    private String oktaIssuer;

    @Value("${OKTA_CLIENT_ID}")
    private String oktaClientId;

    @Value("${OKTA_CLIENT_SECRET}")
    private String oktaClientSecret;

    @Value("${JWK_SET_URI}")
    private String jwkSetUri;

    @Value("${KEY_STORE_PASSWORD}")
    private String sslKeyStorePassword;
}
