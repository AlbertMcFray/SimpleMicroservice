package rest.test.catalogue.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class ProductsRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/products.sql")
    void findProducts_ReturnsProductsList() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/products")
                .param("filter", "product")
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));

        //when
        this.mockMvc.perform(requestBuilder).andExpectAll()
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                       {"id":1,"title":"Product 1","details":"Product description 1"},
                                       {"id":2,"title":"Product 2","details":"Product description 2"},
                                       {"id":3,"title":"Product 3","details":"Product description 3"},
                                       {"id":4,"title":"Product 4","details":"Product description 4"}
                                ]"""));
    }

    @Test
    void createProduct_RequestIsValidReturnsNewProduct() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""" 
                        {"title":"Product 1","details":"Product description 1"}
                        """)
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        header().string(HttpHeaders.LOCATION, "http://localhost/catalogue-api/products/1"),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(""" 
                                {"id" : 1,"title" : "Product 1", "details" : "Product description 1"}
                                """));
    }

    @Test
    void createProduct_RequestIsInvalid_ReturnsProblemDetail() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "  ", "details": null}""")
                .locale(Locale.of("ru", "RU"))
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                      "type":"about:blank",
                                      "title":"Bad Request",
                                      "status":400,
                                      "detail":"Bad request",
                                      "instance":"/catalogue-api/products",
                                      "errors":["Title size must be between 3 and 50 symbols"]
                                }"""));
    }

    @Test
    void createProduct_UserIsNotAuthorized_ReturnsForbidden() throws Exception{
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title" : "  ", "details" : null}
                        """)
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }
}