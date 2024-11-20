package com.prodify.cqrs.UserService.controller;

//import com.prodify.cqrs.CommonService.commands.CreateUserCommand;
import com.prodify.cqrs.CommonService.model.User;
import com.prodify.cqrs.CommonService.queries.GetUserPaymentDetailsQuery;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserController {

//    private final CommandGateway commandGateway;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private transient QueryGateway queryGateway;

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
        public User getUserPaymentDetails(@PathVariable String userId) {
            log.info("Received request to fetch payment details for userId: {}", userId);

            GetUserPaymentDetailsQuery getUserPaymentDetailsQuery = new GetUserPaymentDetailsQuery(userId);

            try {
                log.info("Querying for user payment details with userId: {}", userId);
                User user = queryGateway.query(getUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
                if (user != null) {
                    log.info("Successfully fetched payment details for userId: {}", userId);
                } else {
                    log.warn("No user found with userId: {}", userId);
                }
                return user;
            } catch (Exception e) {
                log.error("Error occurred while fetching payment details for userId: {}", userId, e);
                throw new RuntimeException("Error fetching user payment details", e);  // You can customize this error handling based on your needs
            }
        }


}
