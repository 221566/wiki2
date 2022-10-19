package com.lwx.controller;

import com.lwx.req.UserQueryReq;
import com.lwx.req.UserSaveReq;
import com.lwx.resp.CommonResp;
import com.lwx.resp.UserQueryResp;
import com.lwx.resp.PageResp;
import com.lwx.service.UserService;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

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
}
