package rest.test.manager.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
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
import rest.test.manager.entity.Product;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpPort = 54321)
public class ProductsControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/products.sql")
    void getProductList_ReturnsProductListPage() throws Exception{
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/list")
                .queryParam("filter", "product")
                .with(user("manager").roles("MANAGER"));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/catalogue-api/products"))
                .withQueryParam("filter", WireMock.equalTo("product"))
                .willReturn(WireMock.ok("""
                        [
                          {"id:1, "title":"Product 1", "details" : "Description 1"},
                          {"id:2, "title":"Product 2", "details" : "Description 2"}
                        ]""").withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        this.mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/products/list"),
                        model().attribute("filter", "product"),
                        model().attribute("products", List.of(
                                new Product(1, "Product 1", "Description 1"),
                                new Product(2, "Product 2", "Description 2"))
                        ));
        WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathMatching("/catalogue-api/products"))
                .withQueryParam("filter", WireMock.equalTo("product")));
    }


    @Test
    void getNewProductPage_ReturnProductPage() throws Exception{
        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/create")
                .with(user("manager").roles("MANAGER"));

        //when
        mockMvc.perform(requestBuilder)
        //then
                .andDo(print())
                .andExpectAll(status().isOk(), view().name("catalogue/products/new_product"));
    }
}
