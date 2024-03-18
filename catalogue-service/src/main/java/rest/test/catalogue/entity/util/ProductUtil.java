package rest.test.catalogue.entity.util;

import rest.test.catalogue.entity.Product;
import rest.test.catalogue.entity.dto.ProductDTO;

public abstract class ProductUtil {
    public static ProductDTO convertToDto (Product product){
        return ProductDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .details(product.getDetails())
                .build();
    }
}
