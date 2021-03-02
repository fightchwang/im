package com.design.im.interceptor;

import com.design.im.model.LoginUserInfo;
import com.design.im.model.po.UserPO;
import com.design.im.service.LogOutTokenService;
import com.design.im.service.UserService;
import com.design.im.util.CommonUtils;
import com.design.im.util.JwtUtils;
import com.design.im.util.LoginUserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.design.im.util.Constants.*;

public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private LogOutTokenService logOutTokenService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(TOKEN_HEADER);
        String refreshToken = request.getHeader(TOKEN_HEADER);

        if(StringUtils.isEmpty(token) || StringUtils.isEmpty(refreshToken)){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }


        if(logOutTokenService.isTokenLogout(token)){
            //user have log out
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            throw new IllegalStateException("User have logout");
        }


        LoginUserInfo loginUserInfo = LoginUserHolder.getLoginUserByToken(token);

        if(loginUserInfo == null){
            //user have log in
            //token is expired, or admin reboot the server(so all login user saved in hold lost)
            String userIdStr = JwtUtils.getAudience(refreshToken);

            boolean isValid = false;
            try {


                if(JwtUtils.verifyToken(token, userIdStr) ){
                    //token is valid
                    loginUserInfo = buildLoginUser(Long.valueOf(userIdStr), token, refreshToken, response);
                    LoginUserHolder.login(loginUserInfo);
                    isValid = true;

                }else if (JwtUtils.verifyToken(refreshToken, userIdStr)){
                    //token expired, but refresh token valid
                    loginUserInfo = buildLoginUser(Long.valueOf(userIdStr), null, null, response);
                    LoginUserHolder.login(loginUserInfo);
                    isValid = true;
                }

            }catch (Exception ex){

            }

            if(!isValid){
                //user not login
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                throw new IllegalStateException("User not login");
            }

        }

        //set it to context, so that we can get if from current thread
        LoginUserHolder.setLoginUserToContext(loginUserInfo);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        LoginUserHolder.resetContext();
    }


    private LoginUserInfo buildLoginUser(long userId, String token, String refreshToken, HttpServletResponse response){
        UserPO user = userService.getUserById(userId);
        if(user == null){
            throw new IllegalStateException("user have not login");
        }
        LoginUserInfo loginUserInfo = new LoginUserInfo();
        String username = CommonUtils.getUserName(user.getFirstname(), user.getSurname());
        if(StringUtils.isEmpty(token)){
            token = JwtUtils.createToken(String.valueOf(userId), username, TOKEN_EXPIRE);
            response.addHeader(TOKEN_HEADER, token);
        }
        if(StringUtils.isEmpty(refreshToken)){
            refreshToken = JwtUtils.createToken(String.valueOf(userId), username, REFRESH_TOKEN_EXPIRE);
            response.addHeader(REFRESH_TOKEN_HEADER, refreshToken);
        }
        loginUserInfo.setToken(token);
        loginUserInfo.setRefreshToken(refreshToken);
        loginUserInfo.setUserId(userId);
        loginUserInfo.setUserName(username);
        return loginUserInfo;
    }


}
