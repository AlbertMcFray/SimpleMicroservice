package rest.test.customer.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rest.test.customer.entity.ProductReview;

public interface ProductReviewsService {

    Mono<ProductReview> createProductReview(int productId, int rating, String review);

    Flux<ProductReview> findProductReviewsByProduct(int productId);

}
