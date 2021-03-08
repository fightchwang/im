package com.design.im.api;

import com.alibaba.fastjson.JSONObject;
import com.design.im.model.IMMessage;
import com.design.im.model.LoginUserInfo;
import com.design.im.model.UserVo;
import com.design.im.service.ImmessageService;
import com.design.im.service.TopicService;
import com.design.im.util.LoginUserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint(value = "/api/im/{token}/{refresh_token}")
@Component
@Slf4j
public class IMWebSocket {

    /** 记录当前在线连接数 */
    private static AtomicInteger onlineCount = new AtomicInteger(0);

    private static Map<Long, Session> userIdSessionMap = new ConcurrentHashMap<>();


    @Autowired
    private ImmessageService immessageService;

    @Autowired
    private TopicService topicService;


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token, @PathParam("refresh_token") String refreshToken) {
        onlineCount.incrementAndGet(); // 在线数加1
        Long userId = LoginUserHolder.getUserId(token);
        userIdSessionMap.put(userId, session);
        log.info("有新连接加入：{}，当前在线人数为：{}", session.getId(), onlineCount.get());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("token") String token, @PathParam("refresh_token") String refreshToken) {
        onlineCount.decrementAndGet(); // 在线数减1
        LoginUserInfo user = LoginUserHolder.getLoginUserByToken(token);
        if(user != null){
            userIdSessionMap.remove(user.getUserId());
            LoginUserHolder.logout(user);
        }
        log.info("有一连接关闭：{}，当前在线人数为：{}", session.getId(), onlineCount.get());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message
     *            客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("token") String token, @PathParam("refresh_token") String refreshToken) {
        log.info("服务端收到客户端[{}]的消息:{}", session.getId(), message);
        IMMessage imMessage = JSONObject.parseObject(message, IMMessage.class);
        Long fromUserId = LoginUserHolder.getUserId(token);
        imMessage.setFromUserId(fromUserId);
        imMessage.setTime(System.currentTimeMillis());

        String finalMsg = JSONObject.toJSONString(imMessage);
        if(imMessage != null){
            Long toUserId = imMessage.getToUserId();
            if(!imMessage.isGroupMessage()){
                //单独的消息
                if(userIdSessionMap.get(toUserId) != null){
                    //用户在线，直接发送
                    sendMessage(finalMsg, userIdSessionMap.get(toUserId));
                }

            }else {
                //群聊
                //获取topicId对应的用户
                List<UserVo> users = topicService.getTopicUsers(imMessage.getTopicId());
                if(!CollectionUtils.isEmpty(users)){
                    for (UserVo user : users) {
                        toUserId = user.getUserId();
                        if(userIdSessionMap.get(toUserId) != null){
                            //用户在线
                            sendMessage(finalMsg, userIdSessionMap.get(user.getUserId()));
                        }
                    }

                }

            }

            //存放道数据库里面，以便用户登陆的时候拉取
            immessageService.insertMessage(imMessage, fromUserId);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 服务端发送消息给客户端
     */
    private void sendMessage(String message, Session toSession) {
        try {
            log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
            toSession.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败：{}", e);
        }
    }

}
