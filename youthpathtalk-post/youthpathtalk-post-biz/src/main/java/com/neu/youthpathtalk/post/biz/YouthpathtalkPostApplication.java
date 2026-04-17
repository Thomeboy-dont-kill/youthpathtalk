package com.neu.youthpathtalk.post.biz;

import com.neu.youthpathtalk.distributed.id.generator.client.DistributedIdGenFeignClient;
import com.neu.youthpathtalk.user.api.client.UserServiceFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRetry
@EnableScheduling
@SpringBootApplication
@EnableFeignClients(clients= {DistributedIdGenFeignClient.class, UserServiceFeignClient.class})
public class YouthpathtalkPostApplication {

    public static void main(String[] args) {
        SpringApplication.run(YouthpathtalkPostApplication.class, args);
    }

}
