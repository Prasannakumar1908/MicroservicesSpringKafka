package com.prodify.cqrs.UserService.controller;

//import com.prodify.cqrs.CommonService.commands.CreateUserCommand;
import com.prodify.cqrs.CommonService.model.User;
import com.prodify.cqrs.CommonService.queries.GetUserPaymentDetailsQuery;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.axonframework.queryhandling.responsetypes.ResponseTypes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletionException;

@RestController
@RequestMapping("/users")

public class UserController {

//    private final CommandGateway commandGateway;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final QueryGateway queryGateway;

    public UserController( QueryGateway queryGateway) {
//        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

//    // POST method to create a new user
//    @PostMapping("/user")
//    public String createUser(@RequestBody User user) {
//        CreateUserCommand createUserCommand = new CreateUserCommand(user);
//        // Send the command to the command handler
//        String userId = commandGateway.sendAndWait(createUserCommand);
//        return "User created successfully with ID: " + userId;
//    }


//    @GetMapping("{userId}")
//    public User getUserPaymentDetails(@PathVariable String userId){
//        GetUserPaymentDetailsQuery getUserPaymentDetailsQuery
//                = new GetUserPaymentDetailsQuery(userId);
//        User user =
//                queryGateway.query(getUserPaymentDetailsQuery,
//                        ResponseTypes.instanceOf(User.class)).join();
//
//        return user;
//    }
    @GetMapping("{userId}")
    public ResponseEntity<?> getUserPaymentDetails(@PathVariable String userId) {
        try {
            logger.info("Received request for payment details of user: {}", userId);

            GetUserPaymentDetailsQuery getUserPaymentDetailsQuery = new GetUserPaymentDetailsQuery(userId);
            User user = queryGateway.query(getUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();

            logger.info("Successfully retrieved payment details for user: {}", userId);
            return ResponseEntity.ok(user);
        } catch (CompletionException ex) {
            logger.error("Error retrieving payment details for user {}: {}", userId, ex.getCause().getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving user payment details: " + ex.getCause().getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error for user {}: {}", userId, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + ex.getMessage());
        }
    }


}
