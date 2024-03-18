package rest.test.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rest.test.catalogue.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query(value = "select p from Product p where p.title ilike :filter")
    Iterable<Product> findAllByTitleLikeIgnoreCase(@Param("filter") String filter); // select * from catalogue.t_product where c_title ilike :filter


}
