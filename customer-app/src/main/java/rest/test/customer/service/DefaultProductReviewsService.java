package rest.test.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rest.test.customer.entity.ProductReview;
import rest.test.customer.repository.ProductReviewRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultProductReviewsService implements ProductReviewsService {
    private final ProductReviewRepository productReviewsRepository;
    @Override
    public Mono<ProductReview> createProductReview(int productId, int rating, String review) {
        return this.productReviewsRepository.save(new ProductReview(UUID.randomUUID(), productId, rating, review));
    }

    @Override
    public Flux<ProductReview> findProductReviewsByProduct(int productId) {
        return this.productReviewsRepository.findAllByProductId(productId);
    }
}
