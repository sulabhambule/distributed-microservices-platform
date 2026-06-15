package com.sulabh.api_gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final WebClient webClient;

    public JwtValidationGatewayFilterFactory(WebClient.Builder webClientBuilder, @Value("${auth-service.url}") String authServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
    }

    // exchange = current request
    // chain    = next filter/service
    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
             String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
             if(token == null || !token.startsWith("Bearer ")) {
                 exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                 return exchange.getResponse().setComplete();
             }

             return webClient.get()
                     .uri("/auth/validate")
                     .header(HttpHeaders.AUTHORIZATION, token)
                     .retrieve()
                     .toBodilessEntity()
                     .then(chain.filter(exchange));
        };
    }
}

// notes

//1. Java Filter ImplementationCreate a component that extends AbstractGatewayFilterFactory.
// The name of the class must end with GatewayFilterFactory so that Spring Cloud Gateway can
// map it to its shorter name (JwtValidation) inside your configuration files.

//2) . Configuration (application.yml)Apply your custom validation logic directly to specific
// protected routes under the filters block.


//Core Mechanics
// Naming Conventions: Spring derives the token name in the routing files
// by stripping away the GatewayFilterFactory suffix from your bean class name.
// JwtValidationGatewayFilterFactory becomes JwtValidation.

// Downstream Context Mutation:
// The implementation uses .mutate() on the incoming exchange object.
// This safely attaches verified user contexts (like user IDs or access roles) into custom
// headers (X-User-Id), preventing services downstream from needing to parse the cryptographic
// token a second time.

// Reactive Nature: The onError handler returns a Mono<Void> and utilizes
// non-blocking APIs (response.setComplete()), ensuring your gateway infrastructure retains its
// high-throughput performance profiles under heavy concurrent loads.
