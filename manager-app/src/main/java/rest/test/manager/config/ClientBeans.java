package rest.test.manager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import rest.test.manager.client.RestClientProductsRestClient;

@Configuration
public class ClientBeans {
    @Bean
    RestClientProductsRestClient productsRestClient(@Value("${resttest.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUri){
        return new RestClientProductsRestClient(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .build());
    }
}
