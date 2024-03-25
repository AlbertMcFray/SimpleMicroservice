package rest.test.customer.repository;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rest.test.customer.entity.FavouriteProduct;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Repository
public class InMemoryFavouriteProductRepository implements FavouriteProductRepository {
    private final List<FavouriteProduct> favoriteProducts = Collections.synchronizedList(new LinkedList<>());
    @Override
    public Mono<FavouriteProduct> save(FavouriteProduct favoriteProduct) {
        this.favoriteProducts.add(favoriteProduct);
        return Mono.just(favoriteProduct);
    }

    @Override
    public Mono<Void> deleteByProductId(int productId) {
        this.favoriteProducts.removeIf(favoriteProduct -> favoriteProduct.getProductId() == productId);
        return Mono.empty();
    }

    @Override
    public Mono<FavouriteProduct> findByProductId(int productId) {
        return Flux.fromIterable(this.favoriteProducts)
                .filter(favouriteProduct -> favouriteProduct.getProductId() == productId)
                .singleOrEmpty();
    }

    @Override
    public Flux<FavouriteProduct> findAll() {
        return Flux.fromIterable(this.favoriteProducts);
    }
}
