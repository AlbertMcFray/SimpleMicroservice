package rest.test.feedback.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rest.test.feedback.entity.FavouriteProduct;

public interface FavouriteProductRepository {
    Mono<FavouriteProduct> save(FavouriteProduct favoriteProduct);

    Mono<Void> deleteByProductId(int productId);

    Mono<FavouriteProduct> findByProductId(int productId);

    Flux<FavouriteProduct> findAll();
}
