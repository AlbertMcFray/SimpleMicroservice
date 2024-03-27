package rest.test.customer.entity;

import java.util.UUID;

public record ProductReview(int productId, int rating, String review) {
}
