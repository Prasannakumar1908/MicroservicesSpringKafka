package com.prodify.apigateway;

import com.prodify.apigateway.Model.*;
import com.prodify.apigateway.controller.GatewayController;
import com.prodify.apigateway.util.GatewayServiceDecorator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@WebFluxTest(controllers = GatewayController.class)
class GatewayControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GatewayServiceDecorator gatewayServiceDecorator;

    private OrderRestModel validOrder;
    private OrderRestModel invalidOrder;
    private ProductRestModel validProduct;
    private ProductRestModel invalidProduct;
    private UserModel validUser;

    @BeforeEach
    void setup() {
        // Valid Order
        validOrder = new OrderRestModel();
        validOrder.setProductId("prod1");
        validOrder.setUserId("user1");
        validOrder.setAddressId("addr1");
        validOrder.setQuantity(5);

        // Invalid Order (missing productId, quantity is negative)
        invalidOrder = new OrderRestModel();
        invalidOrder.setUserId("user1");
        invalidOrder.setAddressId("addr1");
        invalidOrder.setQuantity(-1);

        // Valid Product
        validProduct = new ProductRestModel();
        validProduct.setName("Test Product");
        validProduct.setPrice(new BigDecimal("100.00"));
        validProduct.setQuantity(10);

        // Invalid Product (price is null)
        invalidProduct = new ProductRestModel();
        invalidProduct.setName("Test Product");
        invalidProduct.setQuantity(10);

        // Valid User
        validUser = new UserModel();
        validUser.setUserId("user1");
        validUser.setPaymentDetails("1234-5678-9012-3456");
    }

    @Test
    void createOrder_withValidData_shouldReturnCreatedStatus() {
        Mockito.when(gatewayServiceDecorator.execute(
                        Mockito.eq("ORDER_SERVICE_URL"),
                        Mockito.eq("order"),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.any(OrderRestModel.class),
                        Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.CREATED).body("Order Created")));

        webTestClient.post()
                .uri("/gateway/order")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validOrder)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class).isEqualTo("Order Created");
    }

    @Test
    void createOrder_withInvalidData_shouldReturnBadRequest() {
        OrderRestModel invalidOrder = new OrderRestModel();
        invalidOrder.setProductId(null); // Invalid
        invalidOrder.setUserId("user1");
        invalidOrder.setAddressId("addr1");
        invalidOrder.setQuantity(-1); // Invalid

        webTestClient.post()
                .uri("/gateway/order")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidOrder)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors").exists(); // Check for error message in the response body
    }

    @Test
    void addProduct_withValidData_shouldReturnCreatedStatus() {
        Mockito.when(gatewayServiceDecorator.execute(
                        Mockito.eq("PRODUCT_SERVICE_URL"),
                        Mockito.eq("product"),
                        Mockito.eq(HttpMethod.POST),
                        Mockito.any(ProductRestModel.class),
                        Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.CREATED).body("Product Added")));

        webTestClient.post()
                .uri("/gateway/product")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validProduct)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class).isEqualTo("Product Added");
    }

    @Test
    void addProduct_withInvalidData_shouldReturnBadRequest() {
        webTestClient.post()
                .uri("/gateway/product")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidProduct)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors").exists();
    }

    @Test
    void getUserPaymentDetails_shouldReturnUserDetails() {
        Mockito.when(gatewayServiceDecorator.execute(
                        Mockito.eq("USER_SERVICE_URL"),
                        Mockito.eq("user1"),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.isNull(),
                        Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(ResponseEntity.ok(validUser)));

        webTestClient.get()
                .uri("/gateway/users/user1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserModel.class).isEqualTo(validUser);
    }
}
