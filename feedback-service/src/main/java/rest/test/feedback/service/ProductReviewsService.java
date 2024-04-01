package rest.test.feedback.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rest.test.feedback.entity.ProductReview;

public interface ProductReviewsService {

    Mono<ProductReview> createProductReview(int productId, int rating, String review, String userId);

    Flux<ProductReview> findProductReviewsByProduct(int productId);

}
