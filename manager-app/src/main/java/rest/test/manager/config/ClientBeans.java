package rest.test.manager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestClient;
import rest.test.manager.client.RestClientProductsRestClient;

@Configuration
public class ClientBeans {
    @Bean
    RestClientProductsRestClient productsRestClient(
            @Value("${resttest.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUri,
            @Value("${resttest.services.catalogue.username:}") String catalogueUsername,
            @Value("${resttest.services.catalogue.password:}") String cataloguePassword){
        return new RestClientProductsRestClient(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .requestInterceptor(
                        new BasicAuthenticationInterceptor(catalogueUsername, cataloguePassword))
                .build());
    }
}
