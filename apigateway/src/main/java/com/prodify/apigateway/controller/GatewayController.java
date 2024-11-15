//package com.prodify.apigateway.controller;
//
//import com.prodify.apigateway.dto.OrderRestModel;
//import com.prodify.apigateway.dto.ProductRestModel;
//import com.prodify.apigateway.dto.UserDTO;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//@RestController
//@RequestMapping("/gateway")
//public class GatewayController {
//
//    private final WebClient webClient;
//
//    public GatewayController(WebClient webClient) {
//        this.webClient = webClient;
//    }
//
//    // Order Service Endpoints
//    @Tag(name = "Order Service", description = "Operations for managing orders")
//    @Operation(summary = "Create an Order", description = "Creates a new order by delegating to the Order Service")
//    @PostMapping("/orders")
//    public Mono<ResponseEntity<String>> createOrder(@RequestBody OrderRestModel orderRestModel) {
//        return webClient.post()
//                .uri("http://localhost:8083/orders/order")
//                .bodyValue(orderRestModel)
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        response -> response.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
//                .bodyToMono(String.class)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
//    }
//
//    @Tag(name = "Order Service", description = "Operations for managing orders")
//    @Operation(summary = "Get Order Message", description = "Returns a test message from the Order Service")
//    @GetMapping("/orders/message")
//    public Mono<ResponseEntity<String>> getOrderMessage() {
//        return webClient.get()
//                .uri("http://localhost:8083/orders/message")
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        response -> response.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
//                .bodyToMono(String.class)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
//    }
//
//    // Product Service Endpoints
//    @Tag(name = "Product Service", description = "Operations for managing products")
//    @Operation(summary = "Add a Product", description = "Adds a new product by delegating to the Product Service")
//    @PostMapping("/products")
//    public Mono<ResponseEntity<String>> addProduct(@RequestBody ProductRestModel productRestModel) {
//        return webClient.post()
//                .uri("http://localhost:8085/products")
//                .bodyValue(productRestModel)
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        response -> response.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
//                .bodyToMono(String.class)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
//    }
//
//    // User Service Endpoints
//    @Tag(name = "User Service", description = "Operations for managing users")
//    @Operation(summary = "Get User Payment Details", description = "Fetches payment details for a specific user from the User Service")
//    @GetMapping("/users/{userId}")
//    public Mono<ResponseEntity<UserDTO>> getUserPaymentDetails(@PathVariable String userId) {
//        return webClient.get()
//                .uri("http://localhost:8087/users/" + userId)
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        response -> response.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
//                .bodyToMono(UserDTO.class)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)));
//    }
//}


//package com.prodify.apigateway.controller;
//import com.prodify.apigateway.dto.OrderRestModel;
//import com.prodify.apigateway.dto.ProductRestModel;
//import com.prodify.apigateway.dto.UserDTO;
//import com.prodify.apigateway.util.ServiceName;
//import com.prodify.apigateway.util.UriBuilder;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//@RestController
//@RequestMapping("/gateway")
//public class GatewayController {
//
//    private final WebClient webClient;
//
//    public GatewayController(WebClient.Builder webClientBuilder) {
//        this.webClient = webClientBuilder.build();  // Build load-balanced WebClient
//    }
//
//    // Order Service Endpoints
//
//    @Tag(name = "Order Service", description = "Operations for managing orders")
//    @Operation(summary = "Create an Order", description = "Creates a new order by delegating to the Order Service")
//    @PostMapping("/orders")
//    public Mono<ResponseEntity<String>> createOrder(@RequestBody OrderRestModel orderRestModel) {
//
//        String uri = new UriBuilder(ServiceName.ORDER_SERVICE_URL, "order").build();
//        System.out.println("URI: " + uri);
//        return webClient.post()
//                .uri(uri)  // Using lb:// for load balancing
//                .bodyValue(orderRestModel)
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        response -> response.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
//                .bodyToMono(String.class)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
//    }
//
////    @Tag(name = "Order Service", description = "Operations for managing orders")
////    @Operation(summary = "Create an Order", description = "Creates a new order by delegating to the Order Service")
////    @PostMapping("/orders")
////    public Mono<ResponseEntity<String>> createOrder(@RequestBody OrderRestModel orderRestModel) {
////        String uri = new UriBuilder(ServiceName.ORDER_SERVICE_URL, "order").build();
////
////        return webClient.post()
////                .uri(uri)
////                .bodyValue(orderRestModel)
////                .retrieve()
////                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
////                        response -> response.bodyToMono(String.class)
////                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
////                .bodyToMono(String.class)
////                .map(ResponseEntity::ok)
////                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
////    }
//
//
//    @Tag(name = "Order Service", description = "Operations for managing orders")
//    @Operation(summary = "Get Order Message", description = "Returns a test message from the Order Service")
//    @GetMapping("/message")
//    public Mono<ResponseEntity<String>> getOrderMessage() {
//        String uri = new UriBuilder(ServiceName.ORDER_SERVICE_URL, "message").build();
//        return webClient.get()
//                .uri(uri)  // Using lb:// for load balancing //"lb://order-service/orders/message"
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        response -> response.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
//                .bodyToMono(String.class)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
//    }
//
//    // Product Service Endpoints
//
//    @Tag(name = "Product Service", description = "Operations for managing products")
//    @Operation(summary = "Add a Product", description = "Adds a new product by delegating to the Product Service")
//    @PostMapping("/products")
//    public Mono<ResponseEntity<String>> addProduct(@RequestBody ProductRestModel productRestModel) {
//        String uri = new UriBuilder(ServiceName.PRODUCT_SERVICE_URL, "product").build();
//
//        return webClient.post()
//                .uri(uri)  // Using lb:// for load balancing
//                .bodyValue(productRestModel)
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        response -> response.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
//                .bodyToMono(String.class)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
//    }
//
//    // User Service Endpoints
//
//    @Tag(name = "User Service", description = "Operations for managing users")
//    @Operation(summary = "Get User Payment Details", description = "Fetches payment details for a specific user from the User Service")
//    @GetMapping("/users/{userId}")
//    public Mono<ResponseEntity<UserDTO>> getUserPaymentDetails(@PathVariable String userId) {
//        String uri = new UriBuilder(ServiceName.USER_SERVICE_URL, userId).build();
//
//        return webClient.get()
//                .uri(uri)  // Using lb:// for load balancing
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        response -> response.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
//                .bodyToMono(UserDTO.class)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)));
//    }
//}

package com.prodify.apigateway.controller;

import com.prodify.apigateway.dto.OrderRestModel;
import com.prodify.apigateway.dto.ProductRestModel;
import com.prodify.apigateway.dto.SearchRequestDTO;
import com.prodify.apigateway.dto.UserDTO;
import com.prodify.apigateway.util.ServiceName;
import com.prodify.apigateway.util.UriBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/gateway")
@Slf4j
public class GatewayController {

    private final WebClient webClient;

    public GatewayController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();  // Build load-balanced WebClient
    }

    // Order Service Endpoints

    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Create an Order", description = "Creates a new order by delegating to the Order Service")
    @PostMapping("/orders")
    public Mono<ResponseEntity<String>> createOrder(@RequestBody OrderRestModel orderRestModel) {
        String uri = UriBuilder.of(ServiceName.ORDER_SERVICE_URL, "order").build();

        return webClient.post()
                .uri(uri)  // Using lb:// for load balancing
                .bodyValue(orderRestModel)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
    }

    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Get Order", description = "Fetches an order by ID")
    @GetMapping("/orders/{orderId}")
    public Mono<ResponseEntity<OrderRestModel>> getOrder(@PathVariable String orderId) {
        String uri = UriBuilder.of(ServiceName.ORDER_SERVICE_URL, "order/" + orderId).build();
        System.out.println(uri);
        return webClient.get()
                .uri(uri)  // Using lb:// for load balancing
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                .bodyToMono(OrderRestModel.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)));
    }

    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Get All Orders", description = "Fetches all orders through the gateway")
    @GetMapping("/orders")
    public Mono<ResponseEntity<List<OrderRestModel>>> getAllOrders() {
        String uri = UriBuilder.of(ServiceName.ORDER_SERVICE_URL, "orders").build();
        log.info("Fetching all orders from URI: {}", uri);

        return webClient.get()
                .uri(uri)  // Using lb:// for load balancing
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                .bodyToFlux(OrderRestModel.class)
                .collectList()
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error fetching orders", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                });
    }


    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Update an Order", description = "Updates an existing order by delegating to the Order Service")
    @PutMapping("/orders/{orderId}")
    public Mono<ResponseEntity<String>> updateOrder(@PathVariable String orderId, @RequestBody OrderRestModel orderRestModel) {
        String uri = UriBuilder.of(ServiceName.ORDER_SERVICE_URL, "order/" + orderId).build();

        return webClient.put()
                .uri(uri)  // Using lb:// for load balancing
                .bodyValue(orderRestModel)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
    }

    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Delete an Order", description = "Deletes an order by ID")
    @DeleteMapping("/orders/{orderId}")
    public Mono<ResponseEntity<String>> deleteOrder(@PathVariable String orderId) {
        String uri = UriBuilder.of(ServiceName.ORDER_SERVICE_URL, "order/" + orderId).build();

        return webClient.delete()
                .uri(uri)  // Using lb:// for load balancing
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
    }

    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Get Order Message", description = "Returns a test message from the Order Service")
    @GetMapping("/message")
    public Mono<ResponseEntity<String>> getOrderMessage() {
        String uri = UriBuilder.of(ServiceName.ORDER_SERVICE_URL, "message").build();

        return webClient.get()
                .uri(uri)  // Using lb:// for load balancing
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
    }

    // Product Service Endpoints

    @Tag(name = "Product Service", description = "Operations for managing products")
    @Operation(summary = "Add a Product", description = "Adds a new product by delegating to the Product Service")
    @PostMapping("/products")
    public Mono<ResponseEntity<String>> addProduct(@RequestBody ProductRestModel productRestModel) {
        String uri = UriBuilder.of(ServiceName.PRODUCT_SERVICE_URL, "product").build();

        return webClient.post()
                .uri(uri)  // Using lb:// for load balancing
                .bodyValue(productRestModel)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
    }

    // User Service Endpoints

    @Tag(name = "User Service", description = "Operations for managing users")
    @Operation(summary = "Get User Payment Details", description = "Fetches payment details for a specific user from the User Service")
    @GetMapping("/users/{userId}")
    public Mono<ResponseEntity<UserDTO>> getUserPaymentDetails(@PathVariable String userId) {
        String uri = UriBuilder.of(ServiceName.USER_SERVICE_URL, userId).build();

        return webClient.get()
                .uri(uri)  // Using lb:// for load balancing
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                .bodyToMono(UserDTO.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)));
    }
}


