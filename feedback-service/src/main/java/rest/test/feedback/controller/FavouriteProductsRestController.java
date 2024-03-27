package rest.test.feedback.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rest.test.feedback.controller.payload.NewFavouriteProductPayload;
import rest.test.feedback.entity.FavouriteProduct;
import rest.test.feedback.service.FavouriteProductsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("feedback-api/favourite-products")
public class FavouriteProductsRestController {
    private final FavouriteProductsService favouriteProductsService;

    @GetMapping
    public Flux<FavouriteProduct> findFavouritesProducts(){
        return this.favouriteProductsService.findFavouriteProducts();
    }

    @GetMapping("by-product-id/{productId:\\d+}")
    public Mono<FavouriteProduct> findFavouriteProductByProductId(
            @PathVariable("productId") int productId){
        return this.favouriteProductsService.findFavouriteProductByProduct(productId);
    }

    @PostMapping
    public Mono<ResponseEntity<FavouriteProduct>> addProductToFavorites(
            @Valid @RequestBody Mono<NewFavouriteProductPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder){
        return payloadMono.flatMap(payload -> this.favouriteProductsService.addProductToFavorites(payload.productId())
                .map(favouriteProduct -> ResponseEntity
                        .created(uriComponentsBuilder.replacePath("/feedback-api/favourite-products/{id}")
                                .build(favouriteProduct.getId()))
                                .body(favouriteProduct)));
    }

    @DeleteMapping("by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> removeProductFromFavorites(
            @PathVariable("productId") int productId){
        return this.favouriteProductsService.removeProductFromFavorites(productId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
