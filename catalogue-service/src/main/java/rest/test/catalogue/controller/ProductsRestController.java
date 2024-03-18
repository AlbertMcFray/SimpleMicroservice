package rest.test.catalogue.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import rest.test.catalogue.controller.payload.NewProductPayload;
import rest.test.catalogue.entity.Product;
import rest.test.catalogue.entity.dto.ProductDTO;
import rest.test.catalogue.entity.util.ProductUtil;
import rest.test.catalogue.service.ProductService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("catalogue-api/products")
public class ProductsRestController {
    private final ProductService productService;

    @GetMapping
    public List<ProductDTO> findProducts() {
        return this.productService.findAllProducts().stream()
                .map(ProductUtil::convertToDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody NewProductPayload payload,
            BindingResult bindingResult,
            UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            Product product = this.productService.createProduct(payload.title(), payload.details());
            ProductDTO productDTO = ProductUtil.convertToDto(product);
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("/catalogue-api/products/{productId}")
                            .build(Map.of("productId", productDTO.getId())))
                    .body(productDTO);
        }
    }
}
