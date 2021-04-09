package com.design.im.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.design.im.dao.TopicFaqMapper;
import com.design.im.dao.TopicMapper;
import com.design.im.dao.TopicUsersMapper;
import com.design.im.dao.UserMapper;
import com.design.im.model.CreateTopicVo;
import com.design.im.model.MasterInfo;
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
import java.util.Map;
import java.util.Set;
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
        queryWrapper.eq("topicid", topicId);
        return topicFaqMapper.selectList(queryWrapper).stream().peek(item -> item.setAnswer("")).collect(Collectors.toList());
    }

    public TopicFaqPO getAnswer(Long topicFaqId) {
        QueryWrapper<TopicFaqPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", topicFaqId);
        TopicFaqPO faq = topicFaqMapper.selectOne(queryWrapper);
        return faq;
    }

    public void insertUserToTopic(Long topicId, Long userId){
        QueryWrapper<TopicUsersPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topicid", topicId);
        queryWrapper.eq("userid", userId);
        int count = topicUsersMapper.selectCount(queryWrapper);

        if(count > 0){
            //user have already entered this topic, no need to insert a new record
            return;
        }

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
            Set<Long> userIds = usersInDb.stream().map(TopicUsersPO::getUserid).collect(Collectors.toSet());
            userQueryMaaper.in("id", userIds);
            List<UserPO> userPos = userMapper.selectList(userQueryMaaper);
            if(!CollectionUtils.isEmpty(userPos)){
                Map<Long, UserPO> upm = userPos.stream().collect(Collectors.toMap(UserPO::getId, i->i ));
                usersInDb.stream().forEach(item ->{
                    UserVo vo = new UserVo();
                    BeanUtils.copyProperties(item, vo);
                    if(LoginUserHolder.getLoginUserById(item.getUserid()) != null){
                        //this user is online
                        vo.setOnLine(true);
                    }else {
                        vo.setOnLine(false);
                    }
                    UserPO cur = upm.get(item.getUserid());
                    if (cur != null) {
                        vo.setFirstname(cur.getFirstname());
                        vo.setSurname(cur.getSurname());
                        vo.setUserId(item.getUserid());
                    }
                    users.add(vo);
                });
            }

        }

        return users;
    }



    public MasterInfo masterInfo(Long topicId){
        QueryWrapper<TopicPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", topicId);
        TopicPO topicPO  = topicMapper.selectOne(queryWrapper);
        if(topicPO == null){
            throw new RuntimeException(topicId + "Not found");
        }

        QueryWrapper<UserPO> userVoQueryWrapper = new QueryWrapper<>();
        userVoQueryWrapper.eq("id", topicPO.getTopicmaster());
        UserPO userPO = userMapper.selectOne(userVoQueryWrapper);

        if(userPO == null){
            throw new RuntimeException("master not exist for topic: "+ topicId);
        }

        MasterInfo response = new MasterInfo();
        response.setFirstname(userPO.getFirstname());
        response.setLastname(userPO.getSurname());
        response.setFullName(userPO.getFirstname()+" "+ userPO.getSurname());
        response.setUserId(userPO.getId());
        return response;
    }
}
