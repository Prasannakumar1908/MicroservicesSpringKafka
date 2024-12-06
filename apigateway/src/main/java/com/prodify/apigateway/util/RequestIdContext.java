package com.prodify.apigateway.util;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
public class RequestIdContext {

    private static final String REQUEST_ID_KEY = "requestId";

    /**Retrieve the RequestId from the current `Reactor Context`.
     @return a `Mono` with the `RequestId`, or a default value if not present.
     **/
    public Mono<String> getRequestId() {
        return Mono.deferContextual(ctx -> Mono.justOrEmpty(ctx.getOrDefault(REQUEST_ID_KEY, "Unknown-RequestId")));
    }
}
