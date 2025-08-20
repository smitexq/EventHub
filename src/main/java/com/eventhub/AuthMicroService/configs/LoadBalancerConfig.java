package com.eventhub.AuthMicroService.configs;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class LoadBalancerConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancer() {
        return WebClient.builder();
    }

    @Bean
    public WebClient apiGatewayWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://ApiGateway")
                .build();
    }
}
