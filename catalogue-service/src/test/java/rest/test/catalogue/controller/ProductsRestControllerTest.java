package rest.test.catalogue.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.util.UriComponentsBuilder;
import rest.test.catalogue.controller.payload.NewProductPayload;
import rest.test.catalogue.entity.Product;
import rest.test.catalogue.entity.dto.ProductDTO;
import rest.test.catalogue.service.ProductService;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductsRestControllerTest {

    @Mock
    ProductService productService;

    @InjectMocks
    ProductsRestController controller;

    @Test
    void findProduct_ReturnsProductsList() {
        var filter = "filter";
        doReturn(List.of(
                new Product(1, "Product 1", "Product description 1"),
                new Product(2, "Product 2", "Product description 2")
        )).when(this.productService).findAllProducts(filter);

        var result = this.controller.findProducts(filter);

        assertEquals(List.of(
                new ProductDTO(1, "Product 1", "Product description 1"),
                new ProductDTO(2, "Product 2", "Product description 2")), result);
    }

    @Test
    void createProduct_RequestIsValid_ReturnsNoContent() throws BindException {
        var payload = new NewProductPayload("Title", "Details");
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        doReturn(new Product(1, "Title", "Details"))
                .when(this.productService).createProduct(payload.title(), payload.details());

        var result = this.controller.createProduct(payload, bindingResult, uriComponentsBuilder);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(URI.create("http://localhost/catalogue-api/products/1"), result.getHeaders().getLocation());
        assertEquals(new ProductDTO(1, "Title", "Details"), result.getBody());

        verify(this.productService).createProduct("Title", "Details");
        verifyNoMoreInteractions(this.productService);
    }

    @Test
    void createProduct_RequestIsInvalid_ReturnsBadRequest() {
        // given
        var payload = new NewProductPayload("   ", null);
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        bindingResult.addError(new FieldError("payload", "title", "error"));
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        // when
        var exception = assertThrows(BindException.class,
                () -> this.controller.createProduct(payload, bindingResult, uriComponentsBuilder));

        // then
        assertEquals(List.of(new FieldError("payload", "title", "error")), exception.getAllErrors());
        verifyNoInteractions(this.productService);
    }

    @Test
    void createProduct_RequestIsInvalidAndBindResultIsBindException_ReturnsBadRequest() {
        // given
        var payload = new NewProductPayload("   ", null);
        var bindingResult = new BindException(new MapBindingResult(Map.of(), "payload"));
        bindingResult.addError(new FieldError("payload", "title", "error"));
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        // when
        var exception = assertThrows(BindException.class,
                () -> this.controller.createProduct(payload, bindingResult, uriComponentsBuilder));

        // then
        assertEquals(List.of(new FieldError("payload", "title", "error")), exception.getAllErrors());
        verifyNoInteractions(this.productService);
    }
}
