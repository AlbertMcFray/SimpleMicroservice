package rest.test.feedback.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rest.test.feedback.controller.payload.NewProductReviewPayload;
import rest.test.feedback.entity.ProductReview;
import rest.test.feedback.service.ProductReviewsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("feedback-api/product-reviews")
public class ProductReviewRestController {
    private final ProductReviewsService productReviewsService;

    @GetMapping("by-product-id/{productId:\\d+}")
    public Flux<ProductReview> findProductReviewsByProductId(@PathVariable(value = "productId") int productId) {
        return this.productReviewsService.findProductReviewsByProduct(productId);
    }

    @PostMapping
    public Mono<ResponseEntity<ProductReview>> createProductReview(
            @Valid @RequestBody Mono<NewProductReviewPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder) {
        return payloadMono
                .flatMap(payload -> this.productReviewsService.createProductReview(
                        payload.productId(), payload.rating(), payload.review()))
                .map(productReview ->
                        ResponseEntity.created(uriComponentsBuilder.replacePath("/feedback-api/product-reviews/{id}").build(productReview.getId()))
                        .body(productReview));
    }
}
