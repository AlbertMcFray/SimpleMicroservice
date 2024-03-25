package rest.test.customer.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rest.test.customer.entity.ProductReview;

public interface ProductReviewRepository {
    Mono<ProductReview> save(ProductReview productReview);

    Flux<ProductReview> findAllByProductId(int productId);
}
