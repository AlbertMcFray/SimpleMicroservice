package rest.test.customer.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rest.test.customer.entity.FavouriteProduct;

public interface FavouriteProductRepository {
    Mono<FavouriteProduct> save(FavouriteProduct favoriteProduct);

    Mono<Void> deleteByProductId(int productId);

    Mono<FavouriteProduct> findByProductId(int productId);

    Flux<FavouriteProduct> findAll();
}
