package com.design.im.util;

import com.design.im.model.LoginUserInfo;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * a tool class to hold all the login user
 * remove the user once he(she) logout or token expired
 */
public class LoginUserHolder {
    private static Set<LoginUserInfo> loginUsers = Sets.newConcurrentHashSet();
    private static ThreadLocal<LoginUserInfo> loginUserContext = new ThreadLocal<>();

    private static Timer timer = new Timer();

    static {
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                loginUsers.removeIf(user ->{
                    if(JwtUtils.isTokenExpired(user.getToken(), now)){
                        return true;
                    }
                    return false;
                });
            }
        }, 0, 1000);
    }

    public static void login(LoginUserInfo loginUserInfo){
        loginUsers.add(loginUserInfo);
    }

    public static void logout(LoginUserInfo loginUserInfo){
        loginUsers.remove(loginUserInfo);
    }

    public static LoginUserInfo getLoginUserByToken(String token){
        return loginUsers.stream().filter(item -> item.getToken().equals(token)).findAny().orElse(null);
    }

    public static LoginUserInfo getLoginUserById(Long id){
        return loginUsers.stream().filter(item -> Long.valueOf(item.getUserId()).equals(id)).findAny().orElse(null);
    }

    public static void setLoginUserToContext(LoginUserInfo loginUser){
        loginUserContext.set(loginUser);
    }

    public static LoginUserInfo getLoginUserFromContext(){
        return loginUserContext.get();
    }

    public static void resetContext(){
        loginUserContext.remove();
    }

    public static Long getUserId(String token){
        String userId = JwtUtils.getAudience(token);
        return StringUtils.isEmpty(userId) ? null : Long.valueOf(userId);
    }

}
