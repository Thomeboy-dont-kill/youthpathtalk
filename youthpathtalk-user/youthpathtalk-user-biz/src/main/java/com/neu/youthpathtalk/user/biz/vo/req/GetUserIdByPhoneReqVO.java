package com.neu.youthpathtalk.user.biz.vo.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Julien
 * @time 2026/03/09 15:22
 * @description 通过手机号获取userId请求VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserIdByPhoneReqVO {
    @NotBlank(message = "手机号不能为空")
    private String phone;
}