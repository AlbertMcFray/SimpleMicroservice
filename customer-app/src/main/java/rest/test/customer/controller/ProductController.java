package rest.test.customer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import rest.test.customer.client.ProductsClient;
import rest.test.customer.controller.payload.NewProductReviewPayload;
import rest.test.customer.entity.Product;
import rest.test.customer.service.FavouriteProductsService;
import rest.test.customer.service.ProductReviewsService;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products/{productId:\\d+}")
public class ProductController {
    private final ProductsClient productsClient;
    private final FavouriteProductsService favoriteProductsService;
    private final ProductReviewsService productReviewsService;

    @ModelAttribute(name = "product", binding = false)
    public Mono<Product> loadProduct(@PathVariable("productId") int id) {
        return this.productsClient.findProduct(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("{customer.products.error.not_found}")));
    }


    @GetMapping
    public Mono<String> getProductPage(@PathVariable("productId") int id, Model model) {
        model.addAttribute("inFavourite", false);
        return this.productReviewsService.findProductReviewsByProduct(id)
                .collectList()
                .doOnNext(productReviews -> model.addAttribute("reviews", productReviews))
                .then(this.favoriteProductsService.findFavouriteProductByProduct(id)
                        .doOnNext(favouriteProduct -> model.addAttribute("inFavourite", true)))
                .thenReturn("customer/products/product");
    }

    @PostMapping("add-to-favourites")
    public Mono<String> addProductToFavourites(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
                .map(Product::id)
                .flatMap(productId -> this.favoriteProductsService.addProductToFavorites(productId)
                        .thenReturn("redirect:/customer/products/%d".formatted(productId)));
    }

    @PostMapping("delete-from-favourites")
    public Mono<String> deleteProductFromFavourites(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
                .map(Product::id)
                .flatMap(productId -> this.favoriteProductsService.removeProductFromFavorites(productId)
                        .thenReturn("redirect:/customer/products/%d".formatted(productId)));
    }

    @PostMapping("create-review")
    public Mono<String> createReview(
            @PathVariable("productId") int id,
            @Valid NewProductReviewPayload payload,
            BindingResult bindingResult,
            Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("inFavourite", false);
            model.addAttribute("payload", payload);
            model.addAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList()));
            return this.favoriteProductsService.findFavouriteProductByProduct(id)
                            .doOnNext(favouriteProduct -> model.addAttribute("inFavourite", true))
                                    .thenReturn("customer/products/product");
        } else {
            return this.productReviewsService.createProductReview(id, payload.rating(), payload.review())
                    .thenReturn("redirect:/customer/products/%d".formatted(id));

        }
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model){
        model.addAttribute("error", exception.getMessage());
        return "errors/404";
    }
}
