
package com.user.cloud.gan.config.handler;

import com.gan.cloud.enums.ServiceResultEnum;
import com.gan.cloud.exception.UserException;
import com.gan.cloud.pojo.UserToken;
import com.user.cloud.gan.config.annotation.TokenToUser;
import com.user.cloud.gan.dao.UserMapper;
import com.user.cloud.gan.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class TokenToUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserMapper userMapper;

    public TokenToUserMethodArgumentResolver() {
    }

    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(TokenToUser.class)) {
            return true;
        }
        return false;
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        if (parameter.getParameterAnnotation(TokenToUser.class) instanceof TokenToUser) {
            String token = webRequest.getHeader("token");
            if (null != token && !"".equals(token) && token.length() == 32) {
                ValueOperations<String, UserToken> opsForMallUserToken = redisTemplate.opsForValue();
                UserToken userToken = opsForMallUserToken.get(token);
                if (userToken == null ) {
                    UserException.fail(ServiceResultEnum.TOKEN_EXPIRE_ERROR.getResult());
                }
                User user = userMapper.selectByPrimaryKey(userToken.getUserId());
                if (user == null) {
                    UserException.fail(ServiceResultEnum.USER_NULL_ERROR.getResult());
                }
                if (user.getLockedFlag().intValue() == 1) {
                    UserException.fail(ServiceResultEnum.LOGIN_USER_LOCKED_ERROR.getResult());
                }
                return userToken;
            } else {
                UserException.fail(ServiceResultEnum.NOT_LOGIN_ERROR.getResult());
            }
        }
        return null;
    }

    public static byte[] getRequestPostBytes(HttpServletRequest request)
            throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {
            int readlen = request.getInputStream().read(buffer, i,
                    contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return buffer;
    }

}
