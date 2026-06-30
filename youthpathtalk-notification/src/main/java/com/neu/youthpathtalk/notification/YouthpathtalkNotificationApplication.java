package com.neu.youthpathtalk.notification;

import com.neu.youthpathtalk.post.api.client.CommentServiceFeignClient;
import com.neu.youthpathtalk.post.api.client.PostServiceFeignClient;
import com.neu.youthpathtalk.user.api.client.UserServiceFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients= {UserServiceFeignClient.class, CommentServiceFeignClient.class, PostServiceFeignClient.class})
public class YouthpathtalkNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(YouthpathtalkNotificationApplication.class, args);
	}

}
