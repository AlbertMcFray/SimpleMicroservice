package rest.test.customer.repository;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rest.test.customer.entity.ProductReview;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Repository
public class InMemoryProductReviewRepository implements ProductReviewRepository {

    private final List<ProductReview> productReviews = Collections.synchronizedList(new LinkedList<>());
    @Override
    public Mono<ProductReview> save(ProductReview productReview) {
        this.productReviews.add(productReview);
        return Mono.just(productReview);
    }

    @Override
    public Flux<ProductReview> findAllByProductId(int productId) {
        return Flux.fromIterable(this.productReviews)
                .filter(productReview -> productReview.getProductId() == productId);
    }
}
