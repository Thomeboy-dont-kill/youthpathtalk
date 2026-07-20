package com.neu.youthpathtalk.auth.service.impl;

import com.neu.youthpathtalk.auth.cache.RedisService;
import com.neu.youthpathtalk.auth.enums.BizResponseErrorCode;
import com.neu.youthpathtalk.auth.enums.VerifyCodeType;
import com.neu.youthpathtalk.auth.rpc.UserRpcService;
import com.neu.youthpathtalk.auth.service.VerifyCodeService;
import com.neu.youthpathtalk.auth.vo.SendVerifyCodeReqVO;
import com.neu.youthpathtalk.constant.redis.UserRedisKey;
import com.neu.youthpathtalk.exception.BizException;
import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.user.api.vo.req.CheckPhoneRegisteredReqVO;
import com.neu.youthpathtalk.util.CodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Julien
 * @time 2026/03/09 19:05
 * @description 验证码服务实现类
 */
@Service
@RequiredArgsConstructor
public class VerifyCodeServiceImpl implements VerifyCodeService {
    private final UserRpcService userRpcService;
    private final RedisService redisService;
    @Override
    public Response<?> sendVerifyCode(SendVerifyCodeReqVO sendVerifyCodeReqVO) {
        String phone=sendVerifyCodeReqVO.getPhone();
        //构建redisKey,"user:verify:code:"+phone
        String verifyCodeKey= UserRedisKey.verifyCode(phone);
        if (redisService.hasKey(verifyCodeKey)) {
            Long verifyCodeTTL = redisService.getVerifyCodeTTL(verifyCodeKey);
            if (verifyCodeTTL != null && verifyCodeTTL > UserRedisKey.VERIFY_CODE_TTL - UserRedisKey.VERIFY_CODE_SEND_INTERVAL) {
                //如果之前已经发过且没有过期则提示发送频繁
                throw new BizException(BizResponseErrorCode.VERIFY_CODE_SEND_FREQUENTLY);
            }
        }
        CheckPhoneRegisteredReqVO checkPhoneRegisteredReqVO =new CheckPhoneRegisteredReqVO();
        checkPhoneRegisteredReqVO.setPhone(phone);
        Boolean phoneRegistered=userRpcService.checkPhoneRegistered(checkPhoneRegisteredReqVO);
        VerifyCodeType verifyCodeType=sendVerifyCodeReqVO.getType();
        if (phoneRegistered){
            switch (verifyCodeType){
                case LOGIN:
                    String verifyCode= CodeUtil.generateCode();
                    //发送验证码暂时空着
                    redisService.storeVerifyCode(verifyCodeKey,verifyCode);
                    break;
                case REGISTER:
                    throw new BizException(BizResponseErrorCode.USER_PHONE_REGISTERED);
                default:
                    throw new BizException(BizResponseErrorCode.VERIFY_CODE_TYPE_INVALID);
            }
        }else {
            switch (verifyCodeType){
                case LOGIN:
                    throw new BizException(BizResponseErrorCode.USER_PHONE_NOT_REGISTERED);
                case REGISTER:
                    String verifyCode= CodeUtil.generateCode();
                    //发送验证码暂时空着
                    redisService.storeVerifyCode(verifyCodeKey,verifyCode);
                    break;
                default:
                    throw new BizException(BizResponseErrorCode.VERIFY_CODE_TYPE_INVALID);
            }
        }
        return Response.ok();
    }
}
