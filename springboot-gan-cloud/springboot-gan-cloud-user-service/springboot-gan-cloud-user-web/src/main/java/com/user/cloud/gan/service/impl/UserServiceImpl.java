
package com.user.cloud.gan.service.impl;

import com.gan.cloud.dto.PageQueryUtil;
import com.gan.cloud.dto.PageResult;
import com.gan.cloud.enums.ServiceResultEnum;
import com.gan.cloud.exception.UserException;
import com.gan.cloud.pojo.UserToken;
import com.gan.cloud.util.MD5Util;
import com.gan.cloud.util.NumberUtil;
import com.gan.cloud.util.SystemUtil;
import com.user.cloud.gan.controller.param.UserUpdateParam;
import com.user.cloud.gan.dao.UserMapper;
import com.user.cloud.gan.entity.User;
import com.user.cloud.gan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String register(String loginName, String password) {
        if (userMapper.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        User registerUser = new User();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        registerUser.setIntroduceSign("随新所欲，蜂富多彩");
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        registerUser.setPasswordMd5(passwordMD5);
        if (userMapper.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String login(String loginName, String passwordMD5) {
        User user = userMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
        if (user != null) {
            if (user.getLockedFlag() == 1) {
                return ServiceResultEnum.LOGIN_USER_LOCKED_ERROR.getResult();
            }
            //登录后即执行修改token的操作
            String token = getNewToken(System.currentTimeMillis() + "", user.getUserId());
            UserToken mallUserToken = new UserToken();
            mallUserToken.setUserId(user.getUserId());
            mallUserToken.setToken(token);
            ValueOperations<String, UserToken> setToken = redisTemplate.opsForValue();
            setToken.set(token, mallUserToken, 7 * 24 * 60 * 60, TimeUnit.SECONDS);//过期时间7天
            return token;

        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    /**
     * 获取token值
     *
     * @param timeStr
     * @param userId
     * @return
     */
    private String getNewToken(String timeStr, Long userId) {
        String src = timeStr + userId + NumberUtil.genRandomNum(4);
        return SystemUtil.genToken(src);
    }

    @Override
    public Boolean updateUserInfo(UserUpdateParam mallUser, Long userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            UserException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        user.setNickName(mallUser.getNickName());
        //若密码为空字符，则表明用户不打算修改密码，使用原密码保存
        if (!MD5Util.MD5Encode("", "UTF-8").equals(mallUser.getPasswordMd5())) {
            user.setPasswordMd5(mallUser.getPasswordMd5());
        }
        user.setIntroduceSign(mallUser.getIntroduceSign());
        if (userMapper.updateByPrimaryKeySelective(user) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public User getUserDetailByToken(String token) {
        ValueOperations<String, UserToken> opsForUserToken = redisTemplate.opsForValue();
        UserToken mallUserToken = opsForUserToken.get(token);
        if (mallUserToken != null) {
            User mallUser = userMapper.selectByPrimaryKey(mallUserToken.getUserId());
            if (mallUser == null) {
                UserException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
            }
            if (mallUser.getLockedFlag().intValue() == 1) {
                UserException.fail(ServiceResultEnum.LOGIN_USER_LOCKED_ERROR.getResult());
            }
            return mallUser;
        }
        UserException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        return null;
    }

    @Override
    public Boolean logout(String token) {
        redisTemplate.delete(token);
        return true;
    }

    @Override
    public PageResult getUsersPage(PageQueryUtil pageUtil) {
        List<User> mallUsers = userMapper.findUserList(pageUtil);
        int total = userMapper.getTotalUsers(pageUtil);
        PageResult pageResult = new PageResult(mallUsers, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public Boolean lockUsers(Long[] ids, int lockStatus) {
        if (ids.length < 1) {
            return false;
        }
        return userMapper.lockUserBatch(ids, lockStatus) > 0;
    }
}
