
package com.gateway.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UserGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserGatewayApplication.class, args);
    }

}
