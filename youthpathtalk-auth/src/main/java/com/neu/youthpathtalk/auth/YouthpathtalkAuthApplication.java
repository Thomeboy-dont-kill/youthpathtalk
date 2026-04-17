package com.neu.youthpathtalk.auth;

import com.neu.youthpathtalk.user.api.client.UserServiceFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = UserServiceFeignClient.class)
public class YouthpathtalkAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(YouthpathtalkAuthApplication.class, args);
    }

}
