package rest.test.customer.client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rest.test.customer.entity.FavouriteProduct;

public interface FavouriteProductsClient {

    Flux<FavouriteProduct> findFavouriteProducts();

    Mono<FavouriteProduct> findFavouriteProductByProductId(int productId);

    Mono<FavouriteProduct> addProductToFavourites(int productId);

    Mono<Void> removeProductFromFavourites(int productId);
}
