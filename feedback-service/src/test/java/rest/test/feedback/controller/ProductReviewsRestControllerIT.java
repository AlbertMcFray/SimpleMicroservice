package rest.test.feedback.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import rest.test.feedback.entity.ProductReview;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@SpringBootTest
@AutoConfigureWebTestClient
public class ProductReviewsRestControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setUp() {
        this.reactiveMongoTemplate.insertAll(List.of(
                new ProductReview(UUID.fromString("bd7779c2-cb05-11ee-b5f3-df46a1249898"), 1, 1, "Review text 1", "user-1"),
                new ProductReview(UUID.fromString("be424abc-cb05-11ee-ab16-2b747e61f570"), 1, 2, "Review text 2", "user-2"),
                new ProductReview(UUID.fromString("be77f95a-cb05-11ee-91a3-1bdc94fa9de4"), 1, 3, "Review text 3", "user-3")
        )).blockLast();
    }

    @AfterEach
    void tearDown() {
        this.reactiveMongoTemplate.remove(ProductReview.class).all().block();
    }
    @Test
    void findProductReviewsByProductId_ReturnsReviews(){
        //given

        //when
        this.webTestClient
                .mutateWith(mockJwt())
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/1")
                .exchange()
                //then
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("""
                        [
                            {"id": "bd7779c2-cb05-11ee-b5f3-df46a1249898", "productId": 1, "rating": 1, "review": "Review text 1", "userId": "user-1"},
                            {"id": "be424abc-cb05-11ee-ab16-2b747e61f570", "productId": 1, "rating": 2, "review": "Review text 2", "userId": "user-2"},
                            {"id": "be77f95a-cb05-11ee-91a3-1bdc94fa9de4", "productId": 1, "rating": 3, "review": "Review text 3", "userId": "user-3"}
                        ]""");
    }

    @Test
    void findProductReviewsByProductId_UserIsNotAuthenticated_ReturnsNotAuthorized(){
        // given
        // when
        this.webTestClient
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/1")
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }

    @Test
    void createProductReview_RequestIsValid_ReturnsCreatedProductReview(){
        //given

        //when
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "productId": 1,
                            "rating": 3,
                            "review": "Three!"
                        }""")
                // then
                .exchange()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("""
                        {
                            "productId": 1,
                            "rating": 3,
                            "review": "Three!"
                        }""").jsonPath("$.id").exists();
    }

    @Test
    void createProductReview_RequestIsInvalid_ReturnsBadRequest(){
        //given

        //when
        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "productId": null,
                            "rating": -1,
                            "review": ".channel.nio.NioEventLoopGroup@7d088813, socketChannelClass=null, allocator=null, sslContext=null}, streamFactoryFactory=null, commandListeners=[], codecRegistry=ProvidersCodecRegistry{codecProviders=[ValueCodecProvider{}, BsonValueCodecProvider{}, DBRefCodecProvider{}, DBObjectCodecProvider{}, DocumentCodecProvider{}, CollectionCodecProvider{}, IterableCodecProvider{}, MapCodecProvider{}, GeoJsonCodecProvider{}, GridFSFileCodecProvider{}, Jsr310CodecProvider{}, JsonObjectCodecProvider{}, BsonCodecProvider{}, EnumCodecProvider{}, com.mongodb.client.model.mql.ExpressionCodecProvider@134abd78, com.mongodb.Jep395RecordCodecProvider@3b0ed98a, com.mongodb.KotlinCodecProvider@12448de1]}, loggerSettings=LoggerSettings{maxDocumentLength=1000}, clusterSettings={hosts=[localhost:54359], srvServiceName=mongodb, mode=SINGLE, requiredClusterType=UNKNOWN, requiredReplicaSetName='null', serverSelector='null', clusterListeners='[]', serverSelectionTimeout='30000 ms', localThreshold='15 ms'}, socketSettings=SocketSettings{"
                        }""")
                // then
                .exchange()
                .expectHeader().doesNotExist(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                            "errors": [
                                "Product not specified",
                                "Rating less than 1",
                                "The size of the review should not exceed 1000 characters"
                            ]
                        }""");
    }

    @Test
    void createProductReview_UserIsNotAuthenticated_ReturnsNotAuthorized() {
        // given

        // when
        this.webTestClient
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "productId": 1,
                            "rating": 5,
                            "review": "На пяторочку!"
                        }""")
                .exchange()
                // then
                .expectStatus().isUnauthorized();
    }
}
