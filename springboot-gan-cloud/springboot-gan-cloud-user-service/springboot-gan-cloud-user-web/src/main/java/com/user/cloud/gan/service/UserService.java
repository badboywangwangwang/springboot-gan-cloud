/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2022 程序员十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package com.user.cloud.gan.service;

import com.gan.cloud.dto.PageQueryUtil;
import com.gan.cloud.dto.PageResult;
import com.user.cloud.gan.controller.param.UserUpdateParam;
import com.user.cloud.gan.entity.User;

public interface UserService {

    /**
     * 用户注册
     *
     * @param loginName
     * @param password
     * @return
     */
    String register(String loginName, String password);


    /**
     * 登录
     *
     * @param loginName
     * @param passwordMD5
     * @return
     */
    String login(String loginName, String passwordMD5);

    /**
     * 用户信息修改
     *
     * @param mallUser
     * @return
     */
    Boolean updateUserInfo(UserUpdateParam mallUser, Long userId);

    /**
     * 获取用户信息 by token
     *
     * @param token
     * @return
     */
    User getUserDetailByToken(String token);

    /**
     * 登出接口
     * @param token
     * @return
     */
    Boolean logout(String token);

    /**
     * 用户禁用与解除禁用(0-未锁定 1-已锁定)
     *
     * @param ids
     * @param lockStatus
     * @return
     */
    Boolean lockUsers(Long[] ids, int lockStatus);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getUsersPage(PageQueryUtil pageUtil);
}
