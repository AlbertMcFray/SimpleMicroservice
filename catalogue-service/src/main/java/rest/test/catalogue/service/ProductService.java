package rest.test.catalogue.service;

import rest.test.catalogue.entity.Product;
import rest.test.catalogue.entity.dto.ProductDTO;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Iterable<Product> findAllProducts(String filter);

    Product createProduct(String title, String details);

    Optional<ProductDTO> findProduct(int productId);

    void updateProduct(Integer id, String title, String details);

    void deleteProduct(Integer id);
}
