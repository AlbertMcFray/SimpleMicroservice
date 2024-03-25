package rest.test.customer.client;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rest.test.customer.entity.Product;

@RequiredArgsConstructor
public class WebClientProductsClient implements ProductsClient{

    private final WebClient webClient;
    @Override
    public Flux<Product> findAllProducts(String filter) {
        return this.webClient.get().uri("/catalogue-api/products?filter={filter}", filter)
                .retrieve()
                .bodyToFlux(Product.class);
    }

    @Override
    public Mono<Product> findProduct(int id) {
        return this.webClient.get().uri("/catalogue-api/products/{productId}")
                .retrieve()
                .bodyToMono(Product.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }
}
