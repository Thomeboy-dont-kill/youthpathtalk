package com.neu.youthpathtalk.user.biz.vo.req;

import com.neu.youthpathtalk.validator.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/09 15:57
 * @description 发送验证码请求VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckPhoneRegisteredReqVO {
    @PhoneNumber
    private String phone;
}
