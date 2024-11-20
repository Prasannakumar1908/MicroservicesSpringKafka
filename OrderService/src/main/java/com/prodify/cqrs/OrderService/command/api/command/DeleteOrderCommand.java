package com.prodify.cqrs.OrderService.command.api.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteOrderCommand {
    @TargetAggregateIdentifier
    private String orderId;
    private String requestId;
}
