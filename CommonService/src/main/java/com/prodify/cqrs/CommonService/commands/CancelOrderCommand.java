package com.prodify.cqrs.CommonService.commands;

import lombok.Data;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
@Data
public class CancelOrderCommand {

    @TargetAggregateIdentifier
    private String orderId;
    private String orderStatus = "CANCELLED";
}
