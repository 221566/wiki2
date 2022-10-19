package com.lwx.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lwx.domain.User;
import com.lwx.domain.UserExample;
import com.lwx.exception.BusinessException;
import com.lwx.exception.BusinessExceptionCode;
import com.lwx.mapper.UserMapper;
import com.lwx.req.UserLoginReq;
import com.lwx.req.UserQueryReq;
import com.lwx.req.UserResetPasswordReq;
import com.lwx.req.UserSaveReq;
import com.lwx.resp.PageResp;
import com.lwx.resp.UserLoginResp;
import com.lwx.resp.UserQueryResp;
import com.lwx.util.CopyUtil;
import com.lwx.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    @Resource
    private UserMapper userMapper;

    @Autowired
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> selectUser(UserQueryReq userReq)  {
        UserExample userExample = new UserExample();
//        Criteria相当于where
        UserExample.Criteria criteria = userExample.createCriteria();
        if(!ObjectUtils.isEmpty(userReq.getLoginName())) {
            criteria.andLoginNameEqualTo(userReq.getLoginName());
        }

        PageHelper.startPage(userReq.getPage(),userReq.getSize());
        List<User> userList = userMapper.selectByExample(userExample);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        LOG.info("总行数：{}",pageInfo.getTotal());
        LOG.info("总页数：{}",pageInfo.getPages());
//        持久层返回list<User>需要转成List<UserQueryResp>再返回控制层，需要用到循环
//        List<UserQueryResp> respList = new ArrayList<>();
//        for (User user : userList) {
////            UserQueryResp userResp = new UserQueryResp();
////            BeanUtils.copyProperties(user,userResp);
//             对象复制
//            UserQueryResp copy = CopyUtil.copy(user, UserQueryResp.class);
//            respList.add(copy);
//        }
//        列表复制
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);
        PageResp<UserQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void save(UserSaveReq req){
        User user = CopyUtil.copy(req,User.class);
        if (ObjectUtils.isEmpty(req.getId())){
            if (ObjectUtils.isEmpty(selectByLoginName(req.getLoginName()))) {
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            }else {
                //用户名已存在
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        }else {
            /**
             * updateByPrimaryKeySelective加上这个的话user.setLoginName(null);把LoginName清空然后就不会更新这个字段
             */
            user.setLoginName(null);
            user.setPassword(null);
            userMapper.updateByPrimaryKeySelective(user);
        }
    }

    public void delete(Long id){
        userMapper.deleteByPrimaryKey(id);
    }

    public User selectByLoginName(String loginName){
        UserExample userExample = new UserExample();
//        Criteria相当于where
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(loginName);
        List<User> userList = userMapper.selectByExample(userExample);
        if (CollectionUtils.isEmpty(userList)){
            return null;
        }else {
            return userList.get(0);
        }
    }

    public void resetPassword(UserResetPasswordReq req){
        User user = CopyUtil.copy(req,User.class);
        userMapper.updateByPrimaryKeySelective(user);

    }

    public UserLoginResp login(UserLoginReq req){
        User user = selectByLoginName(req.getLoginName());
        if (ObjectUtils.isEmpty(user)){
            //user是空，用户名不存在
            LOG.info("用户名不存在，{}",req.getLoginName());
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        }else {
            if (user.getPassword().equals(req.getPassword())){
                UserLoginResp userLoginResp = CopyUtil.copy(user,UserLoginResp.class);
                return userLoginResp;
            }else {
                //密码不对
                LOG.info("密码不正确，{}",req.getPassword(),user.getPassword());
                throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
            }
        }
    }

}
