package com.design.im.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.design.im.dao.TopicFaqMapper;
import com.design.im.dao.TopicMapper;
import com.design.im.dao.TopicUsersMapper;
import com.design.im.dao.UserMapper;
import com.design.im.model.CreateTopicVo;
import com.design.im.model.UserVo;
import com.design.im.model.po.TopicFaqPO;
import com.design.im.model.po.TopicPO;
import com.design.im.model.po.TopicUsersPO;
import com.design.im.model.po.UserPO;
import com.design.im.util.LoginUserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicService {
    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private TopicFaqMapper topicFaqMapper;

    @Autowired
    private TopicUsersMapper topicUsersMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public long createTopic(CreateTopicVo createTopicVo, long userId){
        if(CollectionUtils.isEmpty(createTopicVo.getQuestions())){
            throw new IllegalArgumentException("question and answer are required");
        }
        QueryWrapper<TopicPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", createTopicVo.getTopic());
        TopicPO topic = topicMapper.selectOne(queryWrapper);
        if(topic != null){
            throw new IllegalArgumentException("name of topic already existed");
        }

        //now insert
        TopicPO po = new TopicPO();
        po.setTopicmaster(userId);
        po.setName(createTopicVo.getTopic());
        topicMapper.insert(po);

        //get the id
        topic = topicMapper.selectOne(queryWrapper);
        if(topic == null){
            throw new IllegalStateException("failed in inserting topic data");
        }

        // insert question
        for (CreateTopicVo.QuestionAndAnswer questionAndAnswer : createTopicVo.getQuestions()) {
            TopicFaqPO faq = new TopicFaqPO();
            faq.setAnswer(questionAndAnswer.getAnswer());
            faq.setQuestion(questionAndAnswer.getQuestion());
            faq.setTopicid(topic.getId());
            topicFaqMapper.insert(faq);
        }

        return topic.getId();
    }


    public List<TopicPO> topicPOList(){
        QueryWrapper<TopicPO> topicPOQueryWrapper = new QueryWrapper<>();
        return topicMapper.selectList(topicPOQueryWrapper);
    }

    public List<TopicFaqPO> topicFaqPOList(Long topicId){
        QueryWrapper<TopicFaqPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", topicId);
        return topicFaqMapper.selectList(queryWrapper).stream().peek(item -> item.setAnswer("")).collect(Collectors.toList());
    }

    public String getAnswer(Long topicFaqId) {
        QueryWrapper<TopicFaqPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", topicFaqId);
        TopicFaqPO faq = topicFaqMapper.selectOne(queryWrapper);
        return faq == null ? "" : faq.getAnswer();
    }

    public void insertUserToTopic(Long topicId, Long userId){
        TopicUsersPO topicUsersPO = new TopicUsersPO();
        topicUsersPO.setTopicid(topicId);
        topicUsersPO.setUserid(userId);
        topicUsersPO.setTime(System.currentTimeMillis());
        topicUsersMapper.insert(topicUsersPO);
    }

    public List<UserVo> getTopicUsers(Long topicId){
        List<UserVo> users = new ArrayList<>();
        QueryWrapper<TopicUsersPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topicid", topicId);
        List<TopicUsersPO> usersInDb = topicUsersMapper.selectList(queryWrapper);
        if(!CollectionUtils.isEmpty(usersInDb)){
            QueryWrapper<UserPO> userQueryMaaper = new QueryWrapper<>();
            List<Long> userIds = usersInDb.stream().map(TopicUsersPO::getUserid).collect(Collectors.toList());
            userQueryMaaper.in("id", userIds);
            List<UserPO> userPos = userMapper.selectList(userQueryMaaper);
            if(!CollectionUtils.isEmpty(userPos)){
                usersInDb.stream().forEach(item ->{
                    UserVo vo = new UserVo();
                    BeanUtils.copyProperties(item, vo);
                    if(LoginUserHolder.getLoginUserById(item.getId()) != null){
                        //this user is online
                        vo.setOnLine(true);
                    }else {
                        vo.setOnLine(false);
                    }
                    vo.setUserId(item.getId());
                    users.add(vo);
                });
            }

        }

        return users;
    }
}
