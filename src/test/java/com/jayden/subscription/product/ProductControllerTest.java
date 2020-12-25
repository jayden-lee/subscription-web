package com.jayden.subscription.product;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductControllerTest {

    @Test
    public void shouldReturnProducts() {
        Product[] products = {
            new Product("netflix", "ott"),
            new Product("watcha", "ott"),
            new Product("wavve", "ott")
        };

        Flux<Product> productFlux = Flux.just(products);

        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        when(productRepository.findAll()).thenReturn(productFlux);

        WebTestClient webTestClient = WebTestClient.bindToController(
            new ProductController(productRepository))
            .build();

        webTestClient.get().uri("/products")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$").isArray()
            .jsonPath("$").isNotEmpty()
            .jsonPath("$[0].name").isEqualTo(products[0].getName())
            .jsonPath("$[0].category").isEqualTo(products[0].getCategory())
            .jsonPath("$[1].name").isEqualTo(products[1].getName())
            .jsonPath("$[1].category").isEqualTo(products[1].getCategory())
            .jsonPath("$[2].name").isEqualTo(products[2].getName())
            .jsonPath("$[2].category").isEqualTo(products[2].getCategory());
    }

    @Test
    public void shouldSaveAProduct() {
        ProductRepository productRepository = Mockito.mock(ProductRepository.class);

        Mono<Product> unsavedProductMono = Mono.just(new Product("netflix","ott"));

        Product savedProduct = new Product("netflix", "ott");
        savedProduct.setId(1L);
        Mono<Product> savedProductMono = Mono.just(savedProduct);

        when(productRepository.save(any())).thenReturn(savedProductMono);

        WebTestClient webTestClient = WebTestClient.bindToController(
            new ProductController(productRepository))
            .build();

        WebTestClient.BodySpec<Product, ?> productBodySpec = webTestClient.post()
            .uri("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .body(unsavedProductMono, Product.class)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Product.class)
            .isEqualTo(savedProduct);
    }
}
