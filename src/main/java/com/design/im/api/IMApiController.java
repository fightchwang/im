package com.design.im.api;

import com.design.im.model.*;
import com.design.im.model.po.UserPO;
import com.design.im.service.ImmessageService;
import com.design.im.service.LogOutTokenService;
import com.design.im.service.TopicService;
import com.design.im.service.UserService;
import com.design.im.util.CommonUtils;
import com.design.im.util.JwtUtils;
import com.design.im.util.LoginUserHolder;
import com.design.im.util.Pbkdf2Sha256Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import static com.design.im.util.Constants.*;

@RestController
@RequestMapping("/api/manage")
@Slf4j
public class IMApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private LogOutTokenService logOutTokenService;

    @Autowired
    private ImmessageService immessageService;


    @PostMapping("/login")
    public CommonResponse login(@RequestBody LoginVo loginVo, HttpServletResponse servletResponse){
        CommonResponse response = new CommonResponse();
        UserPO userPO = userService.getUserByEMail(loginVo.getEmail());
        if(userPO == null){
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMsg("User not exist");
        }else {
            //validate password
            String userPassword = userPO.getPassword();
            if(!Pbkdf2Sha256Util.verification(loginVo.getPassword(), userPassword)){
                response.setCode(HttpStatus.BAD_REQUEST.value());
                response.setMsg("Wrong password");
            }else {
                response.setCode(HttpStatus.OK.value());
                response.setMsg("login success");
                //create jwt token and set it to header

                String username = CommonUtils.getUserName(userPO.getFirstname(), userPO.getSurname());

                String token = JwtUtils.createToken(userPO.getId().toString(), username, TOKEN_EXPIRE);
                String refreshToken = JwtUtils.createToken(userPO.getId().toString(), username, REFRESH_TOKEN_EXPIRE);

                //set response header
                servletResponse.addHeader(TOKEN_HEADER, token);
                servletResponse.addHeader(REFRESH_TOKEN_HEADER, refreshToken);

                //add this into login user holder
                LoginUserInfo info = new LoginUserInfo();
                info.setToken(token);
                info.setRefreshToken(refreshToken);
                info.setUserId(userPO.getId());
                info.setUserName(CommonUtils.getUserName(userPO.getFirstname(), userPO.getSurname()));
                LoginUserHolder.login(info);
            }

        }
        return response;
    }

    @PostMapping("/logout")
    public CommonResponse logout(HttpServletResponse servletResponse){
        LoginUserInfo loginUserInfo = LoginUserHolder.getLoginUserFromContext();
        CommonResponse response = new CommonResponse();
        response.setCode(HttpStatus.OK.value());
        response.setMsg("Success");
        if(loginUserInfo == null){
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMsg("User already log out");
        }else {
            LoginUserHolder.logout(loginUserInfo);
            LoginUserHolder.resetContext();

            logOutTokenService.saveToken(loginUserInfo.getToken());
            //reset header
            servletResponse.addHeader(TOKEN_HEADER, "");
            servletResponse.addHeader(REFRESH_TOKEN_HEADER, "");
        }
        return response;
    }

    @PostMapping("/register")
    public CommonResponse register(@RequestBody UserVo userVo){
        CommonResponse response = new CommonResponse();
        //get hashed password
        String hashedPassowrd = Pbkdf2Sha256Util.encode(userVo.getPassword());
        userVo.setPassword(hashedPassowrd);
        userService.saveUser(userVo);
        response.setCode(HttpStatus.OK.value());
        response.setMsg("User register success");
        return response;
    }


    @PostMapping("/topic/create")
    public CommonResponse createTopic(@RequestBody  CreateTopicVo createTopicVo){
        CommonResponse response = new CommonResponse();
        response.setCode(HttpStatus.OK.value());
        response.setMsg("Success");

        try {
            topicService.createTopic(createTopicVo, LoginUserHolder.getLoginUserFromContext().getUserId());
        }catch (Exception ex){
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMsg(ex.getMessage());
        }

        return response;
    }

    @GetMapping("/topic/list")
    public CommonResponse TopicList(){
        CommonResponse response = new CommonResponse();
        response.setCode(HttpStatus.OK.value());
        response.setMsg("Success");

        try {
            response.setData(topicService.topicPOList());
        }catch (Exception ex){
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMsg("Can't get the topic list");
        }

        return response;
    }

    @GetMapping("/topic/question/list")
    public CommonResponse qustion(@RequestParam Long topicId){
        CommonResponse response = new CommonResponse();
        response.setCode(HttpStatus.OK.value());
        response.setMsg("Success");

        try {
            response.setData(topicService.topicFaqPOList(topicId));
        }catch (Exception ex){
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMsg("Can't get the topic question list");
        }

        return response;
    }

    @PostMapping("/topic/enter/{topicId}")
    public CommonResponse enterTopic(@PathVariable Long topicId){
        CommonResponse response = new CommonResponse();
        response.setCode(HttpStatus.OK.value());
        response.setMsg("Success");

        try {
            topicService.insertUserToTopic(topicId, LoginUserHolder.getLoginUserFromContext().getUserId());
        }catch (Exception ex){
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMsg("Can't get the topic question list");
        }

        return response;
    }

    @GetMapping("/topic/users/{topicId}")
    public CommonResponse topicUsers(@PathVariable Long topicId){
        CommonResponse response = new CommonResponse();
        response.setCode(HttpStatus.OK.value());
        response.setMsg("Success");

        try {
            response.setData(topicService.getTopicUsers(topicId));
        }catch (Exception ex){
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMsg("Can't get the topic user list");
        }

        return response;
    }



    @GetMapping("/topic/question/answer")
    public CommonResponse answer(@RequestParam Long topicFaqId){
        CommonResponse response = new CommonResponse();
        response.setCode(HttpStatus.OK.value());
        response.setMsg("Success");

        try {
            response.setData(topicService.getAnswer(topicFaqId));
        }catch (Exception ex){
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMsg("Can't get the topic question list");
        }

        return response;
    }

    @GetMapping("/profile")
    public CommonResponse profile(){
        LoginUserInfo loginUserInfo = LoginUserHolder.getLoginUserFromContext();
        CommonResponse response = new CommonResponse();
        response.setCode(HttpStatus.OK.value());
        response.setMsg("Success");
        long userId = loginUserInfo.getUserId();
        UserPO user = userService.getUserById(userId);
        //hide password
        user.setPassword("");
        response.setData(user);
        return response;
    }

    /**
     * call it when user plan to send message
     * @return
     */
    @GetMapping("/im/prepare")
    public CommonResponse prepare(){
        CommonResponse response = new CommonResponse();
        response.setCode(HttpStatus.OK.value());
        response.setMsg("Success");
        return response;
    }


    @GetMapping("/message/pull")
    public CommonResponse pullMsg(@RequestParam Boolean isGroupMessage, @RequestParam long topicId,
                                  @RequestParam int page,  @RequestParam int pageSize,
                                  @RequestParam(required = false) Long toUserId){
        CommonResponse response = new CommonResponse();
        response.setCode(HttpStatus.OK.value());
        response.setMsg("Success");

        try {
            Long fromUserId =  LoginUserHolder.getLoginUserFromContext().getUserId();

            response.setData(immessageService.getMessageListOfUser(isGroupMessage, topicId, page, pageSize, toUserId, fromUserId));
        }catch (Exception ex){
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMsg("Can't get the topic question list");
        }

        return response;
    }



}
