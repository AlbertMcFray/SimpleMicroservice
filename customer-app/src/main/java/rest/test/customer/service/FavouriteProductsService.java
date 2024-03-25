package rest.test.customer.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rest.test.customer.entity.FavouriteProduct;
import rest.test.customer.entity.FavouriteProduct;

public interface FavouriteProductsService {
    Mono<FavouriteProduct> addProductToFavorites(int productId);

    Mono<Void> removeProductFromFavorites(int productId);

    Mono<FavouriteProduct> findFavouriteProductByProduct(int productId);

    Flux<FavouriteProduct> findFavouriteProducts();
}
