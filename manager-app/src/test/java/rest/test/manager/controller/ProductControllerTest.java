package rest.test.manager.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ConcurrentModel;
import rest.test.manager.client.BadRequestException;
import rest.test.manager.client.ProductsRestClient;
import rest.test.manager.controller.payload.UpdateProductPayload;
import rest.test.manager.entity.Product;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Modular tests ProductController")
public class ProductControllerTest {

    @Mock
    ProductsRestClient productsRestClient;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    ProductController productController;

    @Test
    void getProduct_ReturnsProductPage_ProductExist() {
        //given
        var product = new Product(1, "Product", "Product desc");

        doReturn(Optional.of(product)).when(this.productsRestClient).findProduct(1);
        //when
        var result = this.productController.product(1);

        //then
        assertEquals(product, result);

        verify(this.productsRestClient).findProduct(1);
        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    void getProduct_ReturnsProductPage_ProductDoesNotExist() {
        //given

        //when
        var exception = assertThrows(NoSuchElementException.class, () -> this.productController.product(1));

        //then
        assertEquals("catalogue.errors.product.not_found", exception.getMessage());

        verify(this.productsRestClient).findProduct(1);
        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    void getProduct_ReturnsProductPage() {
        //given

        //when
        var result = this.productController.getProduct();

        //then
        assertEquals("/catalogue/products/product", result);
    }

    @Test
    void getProductEditPage_ReturnsProductEditPage() {
        //given

        //when
        var result = this.productController.getProductEditPage();

        //then
        assertEquals("catalogue/products/edit", result);
    }

    @Test
    void getProduct_RequestIsValid_ReturnsProductPage() {
        //given
        var product = new Product(1, "Product", "Product desc");
        var model = new ConcurrentModel();
        var payload = new UpdateProductPayload("New title", "New desc");
        var response = new MockHttpServletResponse();

        //when
        var result = this.productController.updateProduct(product, payload, model, response);

        //then
        assertEquals("redirect:/catalogue/products/1", result);

        verify(this.productsRestClient).updateProduct(1, "New title", "New desc");
        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    void getProduct_RequestIsInvalid_ReturnsProductPage() {
        //given
        var product = new Product(1, "Product", "Product desc");
        var model = new ConcurrentModel();
        var payload = new UpdateProductPayload("   ", null);
        var response = new MockHttpServletResponse();

        doThrow(new BadRequestException(List.of("Error 1", "Error 2")))
                .when(this.productsRestClient).updateProduct(1,"   ", null);

        //when
        var result = this.productController.updateProduct(product, payload, model, response);

        //then
        assertEquals("catalogue/products/edit", result);
        assertEquals(payload, model.getAttribute("payload"));
        assertEquals(List.of("Error 1", "Error 2"), model.getAttribute("errors"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        verify(this.productsRestClient).updateProduct(1, "   ", null);
        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    void deleteProduct_RedirectsToProductsListPage() {
        //given
        var product = new Product(1, "Product", "Product desc");

        //when
        var result = this.productController.deleteProduct(product);

        //then
        assertEquals("redirect:/catalogue/products/list", result);

        verify(this.productsRestClient).deleteProduct(1);
        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    void handleNoSuchElementException_Returns404ErrorPage() {
        //given
        var exception = new NoSuchElementException("error");
        var model = new ConcurrentModel();
        var response = new MockHttpServletResponse();
        var locale = Locale.of("en");

        doReturn("Error")
                .when(this.messageSource)
                .getMessage("error", new Object[0], exception.getMessage(), locale);

        //when
        var result = this.productController.handleNoSuchElementException(exception, model, response, locale);

        //then
        assertEquals("catalogue/products/errors/404",  result);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

        verify(this.messageSource).getMessage("error", new Object[0], "error", Locale.of("en"));
        verifyNoMoreInteractions(this.messageSource);
        verifyNoInteractions(this.productsRestClient);

    }
}
