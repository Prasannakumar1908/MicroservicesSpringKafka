package com.prodify.apigateway.controller;

import com.prodify.apigateway.dto.OrderRestModel;
import com.prodify.apigateway.dto.ProductRestModel;
import com.prodify.apigateway.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;





@RestController
@RequestMapping("/gateway")
public class GatewayController {

    private final RestTemplate restTemplate;

    public GatewayController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Order Service Endpoints
    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Create an Order", description = "Creates a new order by delegating to the Order Service")
    @PostMapping("/orders")
    public String createOrder(@RequestBody OrderRestModel orderRestModel) {
        return restTemplate.postForObject("http://localhost:8083/orders/order", orderRestModel, String.class);
    }
    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Get Order Message", description = "Returns a test message from the Order Service")
    @GetMapping("/orders/message")
    public String getOrderMessage() {
        return restTemplate.getForObject("http://localhost:8083/orders/message", String.class);
    }

    // Product Service Endpoints
    @Tag(name = "Product Service", description = "Operations for managing products")
    @Operation(summary = "Add a Product", description = "Adds a new product by delegating to the Product Service")
    @PostMapping("/products")
    public String addProduct(@RequestBody ProductRestModel productRestModel) {
        return restTemplate.postForObject("http://localhost:8085/products", productRestModel, String.class);
    }

    // User Service Endpoints
    @Tag(name = "User Service", description = "Operations for managing users")
    @Operation(summary = "Get User Payment Details", description = "Fetches payment details for a specific user from the User Service")
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDTO> getUserPaymentDetails(@PathVariable String userId) {
        return restTemplate.getForEntity("http://localhost:8087/users/" + userId, UserDTO.class);
    }
}
