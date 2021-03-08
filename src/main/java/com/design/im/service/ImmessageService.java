package com.design.im.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.design.im.dao.ImMessageAtListMapper;
import com.design.im.dao.ImMessageMapper;
import com.design.im.model.IMMessage;
import com.design.im.model.po.ImMessageAtListPO;
import com.design.im.model.po.ImMessagePO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class ImmessageService {
    @Autowired
    private ImMessageMapper messageMapper;

    @Autowired
    private ImMessageAtListMapper imMessageAtListMapper;

    @Transactional
    public void insertMessage(IMMessage imMessage, long fromUserId){
        ImMessagePO messagePO = new ImMessagePO();
        BeanUtils.copyProperties(imMessage, messagePO);
        messagePO.setFromUserId(fromUserId);
        messageMapper.insert(messagePO);

        QueryWrapper<ImMessagePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fromUserId", fromUserId);
        queryWrapper.eq("toUserId", imMessage.getToUserId());
        queryWrapper.eq("topicId", imMessage.getTopicId());
        queryWrapper.eq("isgroupmessage", imMessage.isGroupMessage());
        queryWrapper.eq("time", imMessage.getTime());
        Long id = messageMapper.selectOne(queryWrapper).getId();

        if(!CollectionUtils.isEmpty(imMessage.getAtUserIds())){

            for (Long atUserId : imMessage.getAtUserIds()) {
                ImMessageAtListPO po = new ImMessageAtListPO();
                po.setMsgId(id);
                po.setAtuserid(atUserId);
                imMessageAtListMapper.insert(po);
            }

        }

    }
}
