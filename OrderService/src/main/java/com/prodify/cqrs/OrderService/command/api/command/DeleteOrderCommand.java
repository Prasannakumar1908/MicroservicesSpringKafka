package com.prodify.cqrs.OrderService.command.api.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class DeleteOrderCommand {
    @TargetAggregateIdentifier
    private String orderId;
}
