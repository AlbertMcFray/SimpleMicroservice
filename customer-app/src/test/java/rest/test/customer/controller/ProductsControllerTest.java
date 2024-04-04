package rest.test.customer.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import rest.test.customer.client.FavouriteProductsClient;
import rest.test.customer.client.ProductsClient;
import rest.test.customer.entity.FavouriteProduct;
import rest.test.customer.entity.Product;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductsControllerTest {

    @Mock
    ProductsClient productsClient;

    @Mock
    FavouriteProductsClient favouriteProductsClient;

    @InjectMocks
    ProductsController productsController;

    @Test
    void getProductsListPage_ReturnsProductsListPage(){
        // given
        var model = new ConcurrentModel();

        doReturn(Flux.fromIterable(List.of(
                new Product(1, "Filtered product №1", "Filtered details №1"),
                new Product(2, "Filtered product №2", "Filtered details №2"),
                new Product(3, "Filtered product №3", "Filtered details №3")
        ))).when(this.productsClient).findAllProducts("filter");

        //when
        StepVerifier.create(this.productsController.getProductsListPage(model, "filter"))
                //then
                .expectNext("customer/products/list")
                .verifyComplete();

        assertEquals("filter", model.getAttribute("filter"));
        assertEquals(List.of(
                new Product(1, "Filtered product №1", "Filtered details №1"),
                new Product(2, "Filtered product №2", "Filtered details №2"),
                new Product(3, "Filtered product №3", "Filtered details №3")),
                model.getAttribute("products"));

        verify(this.productsClient).findAllProducts("filter");
        verifyNoMoreInteractions(this.productsClient);
        verifyNoInteractions(this.favouriteProductsClient);
    }

    @Test
    void getFavouriteProductsPage_ReturnsFavouriteProductsPage() {
        // given
        var model = new ConcurrentModel();

        doReturn(Flux.fromIterable(List.of(
                new Product(1, "Filtered product №1", "Filtered details №1"),
                new Product(2, "Filtered product №2", "Filtered details №2"),
                new Product(3, "Filtered product №3", "Filtered details №3")
        ))).when(this.productsClient).findAllProducts("filter");

        doReturn(Flux.fromIterable(List.of(
                new FavouriteProduct(UUID.fromString("a16f0218-cbaf-11ee-9e6c-6b0fa3631587"), 1),
                new FavouriteProduct(UUID.fromString("a42ff37c-cbaf-11ee-8b1d-cb00912914b5"), 3)
        ))).when(this.favouriteProductsClient).findFavouriteProducts();

        // when
        StepVerifier.create(this.productsController.getFavouriteProductsPage(model, "filter"))
                // then
                .expectNext("customer/products/favourites")
                .verifyComplete();

        assertEquals("filter", model.getAttribute("filter"));
        assertEquals(List.of(
                        new Product(1, "Filtered product №1", "Filtered details №1"),
                        new Product(3, "Filtered product №3", "Filtered details №3")),
                model.getAttribute("products"));

        verify(this.productsClient).findAllProducts("filter");
        verify(this.favouriteProductsClient).findFavouriteProducts();
        verifyNoMoreInteractions(this.productsClient, this.favouriteProductsClient);

    }
}
