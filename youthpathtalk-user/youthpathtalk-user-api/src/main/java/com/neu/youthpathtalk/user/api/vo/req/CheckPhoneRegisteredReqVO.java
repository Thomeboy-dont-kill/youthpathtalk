package com.neu.youthpathtalk.user.api.vo.req;

import com.neu.youthpathtalk.validator.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/09 15:57
 * @description 检查手机号是否已注册请求VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckPhoneRegisteredReqVO {
    @PhoneNumber
    private String phone;
}
