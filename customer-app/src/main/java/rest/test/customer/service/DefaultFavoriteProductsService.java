package rest.test.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rest.test.customer.entity.FavouriteProduct;
import rest.test.customer.repository.FavouriteProductRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultFavoriteProductsService implements FavouriteProductsService {

    private final FavouriteProductRepository favoriteProductRepository;

    @Override
    public Mono<FavouriteProduct> addProductToFavorites(int productId) {
        return this.favoriteProductRepository.save(new FavouriteProduct(UUID.randomUUID(), productId));
    }

    @Override
    public Mono<Void> removeProductFromFavorites(int productId) {
        return this.favoriteProductRepository.deleteByProductId(productId);
    }

    @Override
    public Mono<FavouriteProduct> findFavouriteProductByProduct(int productId) {
        return this.favoriteProductRepository.findByProductId(productId);
    }

    @Override
    public Flux<FavouriteProduct> findFavouriteProducts() {
        return this.favoriteProductRepository.findAll();
    }
}
