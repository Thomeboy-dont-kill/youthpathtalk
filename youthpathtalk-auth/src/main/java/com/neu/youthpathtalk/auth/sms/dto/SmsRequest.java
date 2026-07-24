package com.neu.youthpathtalk.auth.sms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
/**
 * @author Julien
 * @time 2026/07/24 21:13
 * @description
 */

@Data
public class SmsRequest {
    @JsonProperty("phoneNumbers")
    private List<String> phoneNumbers;

    @JsonProperty("textMessage")
    private TextMessage textMessage;

    @Data
    public static class TextMessage {
        private String text;
    }
}