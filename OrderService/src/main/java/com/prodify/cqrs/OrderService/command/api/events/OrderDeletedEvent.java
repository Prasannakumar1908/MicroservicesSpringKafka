package com.prodify.cqrs.OrderService.command.api.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDeletedEvent {
    private String orderId;
}
