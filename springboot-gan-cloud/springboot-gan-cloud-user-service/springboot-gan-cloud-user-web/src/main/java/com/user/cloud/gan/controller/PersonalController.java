
package com.user.cloud.gan.controller;

import com.gan.cloud.dto.Result;
import com.gan.cloud.dto.ResultGenerator;
import com.gan.cloud.enums.ServiceResultEnum;
import com.gan.cloud.pojo.UserToken;
import com.gan.cloud.util.BeanUtil;
import com.gan.cloud.util.NumberUtil;
import com.user.cloud.gan.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.user.cloud.gan.config.annotation.TokenToUser;
import com.user.cloud.gan.controller.param.UserLoginParam;
import com.user.cloud.gan.controller.param.UserRegisterParam;
import com.user.cloud.gan.controller.param.UserUpdateParam;
import com.user.cloud.gan.controller.vo.UserVO;
import com.user.cloud.gan.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@Api(value = "v1", tags = "新蜂商城用户操作相关接口")
@RequestMapping("/users/mall")
public class PersonalController {

    @Resource
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(PersonalController.class);

    @PostMapping("/login")
    @ApiOperation(value = "登录接口", notes = "返回token")
    public Result<String> login(@RequestBody @Valid UserLoginParam mallUserLoginParam) {
        if (!NumberUtil.isPhone(mallUserLoginParam.getLoginName())){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_IS_NOT_PHONE.getResult());
        }
        String loginResult = userService.login(mallUserLoginParam.getLoginName(), mallUserLoginParam.getPasswordMd5());

        logger.info("login api,loginName={},loginResult={}", mallUserLoginParam.getLoginName(), loginResult);

        //登录成功
        if (!StringUtils.isEmpty(loginResult) && loginResult.length() == 32) {
            Result result = ResultGenerator.genSuccessResult();
            result.setData(loginResult);
            return result;
        }
        //登录失败
        return ResultGenerator.genFailResult(loginResult);
    }


    @PostMapping("/logout")
    @ApiOperation(value = "登出接口", notes = "清除token")
    public Result<String> logout(@TokenToUser UserToken loginUserToken) {
        Boolean logoutResult = userService.logout(loginUserToken.getToken());

        logger.info("logout api,loginUser={}", loginUserToken.getUserId());

        //登出成功
        if (logoutResult) {
            return ResultGenerator.genSuccessResult();
        }
        //登出失败
        return ResultGenerator.genFailResult("logout error");
    }


    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "")
    public Result register(@RequestBody @Valid UserRegisterParam mallUserRegisterParam) {
        if (!NumberUtil.isPhone(mallUserRegisterParam.getLoginName())){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_IS_NOT_PHONE.getResult());
        }
        String registerResult = userService.register(mallUserRegisterParam.getLoginName(), mallUserRegisterParam.getPassword());

        logger.info("register api,loginName={},loginResult={}", mallUserRegisterParam.getLoginName(), registerResult);

        //注册成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(registerResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //注册失败
        return ResultGenerator.genFailResult(registerResult);
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改用户信息", notes = "")
    public Result updateInfo(@RequestBody @ApiParam("用户信息") UserUpdateParam mallUserUpdateParam, @TokenToUser UserToken loginUserToken) {
        Boolean flag = userService.updateUserInfo(mallUserUpdateParam, loginUserToken.getUserId());
        if (flag) {
            //返回成功
            Result result = ResultGenerator.genSuccessResult();
            return result;
        } else {
            //返回失败
            Result result = ResultGenerator.genFailResult("修改失败");
            return result;
        }
    }

    @GetMapping("/detail")
    @ApiOperation(value = "获取用户信息", notes = "")
    public Result<UserVO> getUserDetail(@TokenToUser UserToken loginUserToken) {
        UserVO mallUserVO = new UserVO();
        User user = userService.getUserDetailByToken(loginUserToken.getToken());
        BeanUtil.copyProperties(user, mallUserVO);
        return ResultGenerator.genSuccessResult(mallUserVO);
    }

    @RequestMapping(value = "/getDetailByToken", method = RequestMethod.GET)
    public Result getUserByToken(@RequestParam("token") String token) {
        User userDetailByToken = userService.getUserDetailByToken(token);
        if (userDetailByToken != null) {
            Result result = ResultGenerator.genSuccessResult();
            result.setData(userDetailByToken);
            return result;
        }
        return ResultGenerator.genFailResult("无此用户数据");
    }
}
