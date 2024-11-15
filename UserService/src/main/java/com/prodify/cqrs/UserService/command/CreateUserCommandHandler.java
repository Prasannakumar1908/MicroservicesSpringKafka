//package com.prodify.cqrs.UserService.command;
//
//import com.prodify.cqrs.CommonService.commands.CreateUserCommand;
//import com.prodify.cqrs.CommonService.model.User;
//import com.prodify.cqrs.UserService.repository.UserRepository;
//import org.axonframework.commandhandling.CommandHandler;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CreateUserCommandHandler {
//
//    private final UserRepository userRepository;
//
//    public CreateUserCommandHandler(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @CommandHandler
//    public String handle(CreateUserCommand command) {
//        User user = command.getUser();
//        // Here we save the user to the database (you could also apply any additional logic as needed)
//        userRepository.save(user);
//
//        // Return the user ID after successfully saving
//        return user.getUserId();
//    }
//}
