package rest.test.manager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;
import rest.test.manager.client.RestClientProductsRestClient;
import rest.test.manager.security.OAuthClientHttpRequestInterceptor;

@Configuration
public class ClientBeans {
    @Bean
    RestClientProductsRestClient productsRestClient(
            @Value("${resttest.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUri,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository auth2AuthorizedClientRepository,
            @Value("${resttest.services.catalogue.registration-id:keycloak}") String registrationId){
        return new RestClientProductsRestClient(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .requestInterceptor(new OAuthClientHttpRequestInterceptor(
                        new DefaultOAuth2AuthorizedClientManager(
                                clientRegistrationRepository,
                                auth2AuthorizedClientRepository),
                        registrationId))
                .build());
    }
}
