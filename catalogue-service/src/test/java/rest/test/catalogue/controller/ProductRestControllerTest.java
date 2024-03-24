package rest.test.catalogue.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import rest.test.catalogue.controller.payload.UpdateProductPayload;
import rest.test.catalogue.entity.dto.ProductDTO;
import rest.test.catalogue.service.ProductService;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductRestControllerTest {
    @Mock
    ProductService productService;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    ProductRestController productRestController;

    @Test
    void getProduct_ProductExists_ReturnsProduct() {
        var product = new ProductDTO(1, "Product 1", "Product description 1");

        doReturn(Optional.of(product)).when(this.productService).findProduct(1);

        var result = this.productRestController.getProduct(1);

        assertEquals(product, result);
    }

    @Test
    void getProduct_ProductDoesNotExist_ThrowsNoSuchElementException() {
        var exception = assertThrows(NoSuchElementException.class, () -> this.productRestController.getProduct(1));

        assertEquals("catalogue.errors.product.not_found", exception.getMessage());
    }

    @Test
    void findProduct_ReturnsProduct() {
        var product = new ProductDTO(1, "Product 1", "Product description 1");

        var result = this.productRestController.findProduct(product);

        assertEquals(product, result);
    }

    @Test
    void updateProduct_RequestIsValid_ReturnsNoContent() throws BindException {
        var payload = new UpdateProductPayload("New title", "New desc");
        var bindingResult = new MapBindingResult(Map.of(), "payload");

        var result = this.productRestController.updateProduct(1, payload, bindingResult);

        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());

        verify(this.productService).updateProduct(1,"New title", "New desc");
    }

    @Test
    void updateProduct_RequestIsInvalid_ReturnsBadRequest() {
        var payload = new UpdateProductPayload("  ", null);
        var bindingResult = new MapBindingResult(Map.of(), "payload");
        bindingResult.addError(new FieldError("payload", "title", "error"));

        var exception = assertThrows(
                BindException.class,
                () -> this.productRestController.updateProduct(1, payload, bindingResult));

        assertEquals(List.of(new FieldError("payload", "title", "error")), exception.getAllErrors());
        verifyNoInteractions(this.productService);
    }

    @Test
    void updateProduct_RequestIsInvalidAndBindResultIsBindException_ReturnsBadRequest() {
        var payload = new UpdateProductPayload("   ", null);
        var bindingResult = new BindException(new MapBindingResult(Map.of(), "payload"));
        bindingResult.addError(new FieldError("payload", "title", "error"));

        var exception = assertThrows(
                BindException.class,
                () -> this.productRestController.updateProduct(1, payload, bindingResult));

        assertEquals(List.of(new FieldError("payload", "title", "error")), exception.getAllErrors());
        verifyNoInteractions(this.productService);
    }

    @Test
    void deleteProduct_ReturnsNoContent() {
        var result = this.productRestController.deleteProduct(1);

        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(this.productService).deleteProduct(1);
    }

    @Test
    void handleNoSuchElementException_ReturnsNotFound() {
        var exception = new NoSuchElementException("error_code");
        var locale = Locale.of("ru");

        doReturn("error details")
                .when(this.messageSource)
                .getMessage("error_code", new Object[0], "error_code", Locale.of("ru"));

        var result = this.productRestController.handleNoSuchElementException(exception, locale);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getBody().getStatus());
        assertEquals("error details", result.getBody().getDetail());
        assertInstanceOf(ProblemDetail.class, result.getBody());
    }
}
