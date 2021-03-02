package com.design.im.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.design.im.dao.UserMapper;
import com.design.im.model.UserVo;
import com.design.im.model.po.UserPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public UserPO getUserByEMail(String email){
        QueryWrapper<UserPO> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        return userMapper.selectOne(wrapper);
    }

    public UserPO getUserById(Long id){
        return userMapper.selectById(id);
    }

    public void saveUser(UserVo userVo){
        UserPO userPO = new UserPO();
        userPO.setFirstname(userVo.getFirstname());
        userPO.setSurname(userVo.getSurname());
        userPO.setPassword(userVo.getPassword());
        userPO.setEmail(userVo.getEmail());
        userMapper.insert(userPO);
    }

}
