package rest.test.feedback.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rest.test.feedback.entity.FavouriteProduct;

import java.util.UUID;

public interface FavouriteProductRepository extends ReactiveCrudRepository<FavouriteProduct, UUID> {
    Mono<Void> deleteByProductIdAndUserId(int productId, String userId);

    Mono<FavouriteProduct> findByProductIdAndUserId(int productId, String userId);

    Flux<FavouriteProduct> findAllByUserId(String userId);
}
