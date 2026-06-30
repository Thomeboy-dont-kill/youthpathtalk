package com.neu.youthpathtalk.user.biz;

import com.neu.youthpathtalk.distributed.id.generator.client.DistributedIdGenFeignClient;
import com.neu.youthpathtalk.post.api.client.PostServiceFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableFeignClients(clients= {DistributedIdGenFeignClient.class, PostServiceFeignClient.class})
public class YouthpathtalkUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(YouthpathtalkUserApplication.class, args);
    }

}
