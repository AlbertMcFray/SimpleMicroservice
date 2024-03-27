package rest.test.customer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import rest.test.customer.client.WebClientFavouriteProductsClient;
import rest.test.customer.client.WebClientProductReviewsClient;
import rest.test.customer.client.WebClientProductsClient;

@Configuration
public class ClientConfig {

    @Bean
    public WebClientProductsClient webClientProductsClient(
            @Value("${resttest.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUrl
    ){
        return new WebClientProductsClient(WebClient.builder()
                .baseUrl(catalogueBaseUrl)
                .build());
    }

    @Bean
    public WebClientFavouriteProductsClient webClientFavouriteProductsClient(
            @Value("${resttest.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl
    ){
        return new WebClientFavouriteProductsClient(WebClient.builder()
                .baseUrl(feedbackBaseUrl)
                .build());
    }

    @Bean
    public WebClientProductReviewsClient webClientProductReviewsClient(
            @Value("${resttest.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl
    ){
        return new WebClientProductReviewsClient(WebClient.builder()
                .baseUrl(feedbackBaseUrl)
                .build());
    }
}
