package com.neu.youthpathtalk;

import com.neu.youthpathtalk.post.api.client.PostServiceFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients= PostServiceFeignClient.class)
public class YouthpathtalkSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(YouthpathtalkSearchApplication.class, args);
    }

}
