package rest.test.catalogue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rest.test.catalogue.entity.Product;
import rest.test.catalogue.entity.dto.ProductDTO;
import rest.test.catalogue.entity.util.ProductUtil;
import rest.test.catalogue.repository.ProductRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultProductService implements ProductService{
    private final ProductRepository productRepository;

    @Override
    public Iterable<Product> findAllProducts(String filter) {
        if(filter != null && !filter.isBlank()){
            return this.productRepository.findAllByTitleLikeIgnoreCase("%" + filter + "%");
        }
        return this.productRepository.findAll();
    }

    @Override
    @Transactional
    public Product createProduct(String title, String details) {
        return this.productRepository.save(new Product(null, title, details));
    }

    @Override
    public Optional<ProductDTO> findProduct(int productId) {
        Optional<Product> productDTO = productRepository.findById(productId);
        return productDTO.map(ProductUtil::convertToDto);
    }

    @Override
    @Transactional
    public void updateProduct(Integer id, String title, String details) {
        this.productRepository.findById(id).
                ifPresentOrElse(product -> {
                    product.setTitle(title);
                    product.setDetails(details);
                }, () -> {
                    throw new NoSuchElementException();
                });
    }

    @Override
    @Transactional
    public void deleteProduct(Integer id) {
        this.productRepository.deleteById(id);

    }
}
