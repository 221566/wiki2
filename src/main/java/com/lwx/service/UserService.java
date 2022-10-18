package com.lwx.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lwx.domain.User;
import com.lwx.domain.UserExample;
import com.lwx.mapper.UserMapper;
import com.lwx.req.UserQueryReq;
import com.lwx.req.UserSaveReq;
import com.lwx.resp.UserQueryResp;
import com.lwx.resp.PageResp;
import com.lwx.util.CopyUtil;
import com.lwx.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
            user.setId(snowFlake.nextId());
            userMapper.insert(user);
        }else {
            userMapper.updateByPrimaryKey(user);
        }
    }

    public void delete(Long id){
        userMapper.deleteByPrimaryKey(id);
    }
}
