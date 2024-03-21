package rest.test.manager.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ConcurrentModel;
import rest.test.manager.client.BadRequestException;
import rest.test.manager.client.ProductsRestClient;
import rest.test.manager.controller.payload.NewProductPayload;
import rest.test.manager.entity.Product;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Modular tests ProductsController")
class ProductsControllerTest {

    @Mock
    ProductsRestClient productsRestClient;

    @InjectMocks
    ProductsController productsController;

    @Test
    void getProductsList_ReturnsProductsListPage(){
        //given
        var model = new ConcurrentModel();
        var filter = "product";
        var products = IntStream.range(1, 4)
                .mapToObj(i -> new Product(i, "Product №%d".formatted(i), "Product description".formatted(i)))
                .toList();

        doReturn(products).when(this.productsRestClient).findAllProducts(filter);

        //when
        var result = this.productsController.getProductsList(model, filter);

        //then
        assertEquals("catalogue/products/list", result);
        assertEquals(filter, model.getAttribute("filter"));
        assertEquals(products, model.getAttribute("products"));
    }

    @Test
    void getNewProductPage_ReturnsNewProductPage () {
        // given

        // when
        var result = this.productsController.getNewProductPage();

        // then
        assertEquals("catalogue/products/new_product", result);
    }

    @Test
    @DisplayName("createProduct will create a new product and redirect to the product page")
    void createProduct_RequestIsValid_ReturnsRedirectionToProductPage() {
        // given
        var payload = new NewProductPayload("Test product", "Test description");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doReturn(new Product(1, "Test product", "Test description"))
                .when(this.productsRestClient)
                .createProduct("Test product", "Test description");

        // when
        var result = this.productsController.createProduct(payload, model, response);

        // then
        assertEquals("redirect:/catalogue/products/1", result);

        verify(this.productsRestClient).createProduct("Test product", "Test description");
        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    @DisplayName("createProduct will return an error page if the request is invalid")
    void createProduct_RequestIsInvalid_ReturnsProductFormWithErrors() {
        // given
        var payload = new NewProductPayload("  ", null);
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();

        doThrow(new BadRequestException(List.of("Error 1", "Error 2")))
                .when(this.productsRestClient)
                .createProduct("  ", null);

        // when
        var result = this.productsController.createProduct(payload, model, response);

        // then
        assertEquals("catalogue/products/new_product", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("Error 1", "Error 2"), model.getAttribute("errors"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        verify(this.productsRestClient).createProduct("  ", null);
        verifyNoMoreInteractions(this.productsRestClient);
    }
}