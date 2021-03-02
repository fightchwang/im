package com.design.im.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.design.im.dao.LogoutTokenMapper;
import com.design.im.model.po.LogoutTokenPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LogOutTokenService {
    @Autowired
    private LogoutTokenMapper logoutTokenMapper;

    public boolean isTokenLogout(String token){
        if(StringUtils.isEmpty(token)) {
            return false;
        }
        QueryWrapper<LogoutTokenPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token", token);
        return logoutTokenMapper.selectCount(queryWrapper) == 1;
    }

    public void saveToken(String token){
        LogoutTokenPO tokenPO = new LogoutTokenPO();
        tokenPO.setToken(token);
        logoutTokenMapper.insert(tokenPO);
    }
}
