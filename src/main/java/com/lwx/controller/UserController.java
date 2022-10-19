package com.lwx.controller;

import com.alibaba.fastjson.JSONObject;
import com.lwx.req.UserLoginReq;
import com.lwx.req.UserQueryReq;
import com.lwx.req.UserResetPasswordReq;
import com.lwx.req.UserSaveReq;
import com.lwx.resp.CommonResp;
import com.lwx.resp.PageResp;
import com.lwx.resp.UserLoginResp;
import com.lwx.resp.UserQueryResp;
import com.lwx.service.UserService;
import com.lwx.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private SnowFlake snowFlake;

    @GetMapping("/selectUser")
    public CommonResp selectUser(@Valid UserQueryReq userReq){
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.selectUser(userReq);

        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    /**
     *1、 @RequestBody对应的就是json方式的（post）提交就像我们写的user
     * 这个是用content-ttype是appliction/json方式的提交，我们需要用到这个注解才能接收到
     *
     * 2、是用www-form方式传参的话就不需要任何注解，直接写变量就能接收到了
     *
     * 3、axios的post提交是用appliction/json方式提交的所以要使用@RequestBody注解才能接收到数据
     */
    public CommonResp save(@Valid @RequestBody UserSaveReq userSaveReq){
        userSaveReq.setPassword(DigestUtils.md5DigestAsHex(userSaveReq.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.save(userSaveReq);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }

    @PostMapping("/resetPassword")
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req){
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }

    @PostMapping("/login")
    public CommonResp login(@Valid @RequestBody UserLoginReq req){
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp userLoginResp = userService.login(req);

        //生成单点登录token并放入redis中
        Long token = snowFlake.nextId();
        LOG.info("生成单点登录token：{}，并放入redis中", token);
        userLoginResp.setToken(token.toString());
        redisTemplate.opsForValue().set(token, JSONObject.toJSON(userLoginResp),3600 * 24, TimeUnit.SECONDS);
        resp.setContent(userLoginResp);
        return resp;
    }
}
