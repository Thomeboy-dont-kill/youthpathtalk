package com.neu.youthpathtalk.auth.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;
/**
 * @author Julien
 * @time 2026/07/24 21:14
 * @description
 */

@Slf4j
@Service
public class SmsGatewayService {

    private final RestTemplate restTemplate;
    private final String gatewayUrl;
    private final String username;
    private final String password;

    public SmsGatewayService(@Value("${sms.gateway.url}") String gatewayUrl,
                             @Value("${sms.gateway.username}") String username,
                             @Value("${sms.gateway.password}") String password) {
        this.restTemplate = new RestTemplate();
        this.gatewayUrl = gatewayUrl;
        this.username = username;
        this.password = password;
    }

    public boolean sendSms(String phoneNumber, String messageText) {
        // 1. 构建请求体
        SmsRequest request = new SmsRequest();
        request.setPhoneNumbers(Collections.singletonList(phoneNumber));
        SmsRequest.TextMessage textMessage = new SmsRequest.TextMessage();
        textMessage.setText(messageText);
        request.setTextMessage(textMessage);

        // 2. 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 3. 设置认证 (Basic Auth)
        // 你的密码就是之前在APP中设置的那个
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);

        // 4. 发送请求
        HttpEntity<SmsRequest> entity = new HttpEntity<>(request, headers);
        String apiUrl = gatewayUrl + "/3rdparty/v1/messages";

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("短信发送成功: {}", response.getBody());
                return true;
            } else {
                log.error("短信发送失败，状态码: {}", response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("调用短信网关异常", e);
            return false;
        }
    }
}