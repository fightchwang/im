package com.design.im.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.design.im.dao.ImMessageAtListMapper;
import com.design.im.dao.ImMessageMapper;
import com.design.im.model.IMMessage;
import com.design.im.model.MessageQueryDto;
import com.design.im.model.PullMsgVo;
import com.design.im.model.UserVo;
import com.design.im.model.po.ImMessageAtListPO;
import com.design.im.model.po.ImMessagePO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        messagePO.setIsgroupmessage(imMessage.getGroupMessage());
        messagePO.setFromUserId(fromUserId);
        messagePO.setMsgcontent(imMessage.getMsgContent());
        messageMapper.insert(messagePO);

        QueryWrapper<ImMessagePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fromUserId", fromUserId);
        queryWrapper.eq("toUserId", imMessage.getToUserId());
        queryWrapper.eq("topicId", imMessage.getTopicId());
        queryWrapper.eq("isgroupmessage", imMessage.getGroupMessage());
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

    public PullMsgVo<ImMessagePO> getMessageListOfUser(Boolean isGroupMessage, Long topicId, int page, int pageSize, Long toUserId, Long fromUserId){
        QueryWrapper<ImMessagePO> queryWrapper = new QueryWrapper<>();
        PullMsgVo<ImMessagePO> response = new PullMsgVo<ImMessagePO>();
        if(page <= 0){
            page = 1;
        }
        if(pageSize < 10){
            pageSize = 10;
        }


        response.setPage(page);
        response.setPageSize(pageSize);

        queryWrapper.eq("isgroupmessage", isGroupMessage);
        queryWrapper.eq("topicId", topicId);
        queryWrapper.orderByDesc("id");
        if(Boolean.TRUE.equals(isGroupMessage)){
            Page<ImMessagePO> pager = new Page<>(page, pageSize);
            Page<ImMessagePO> result = messageMapper.selectPage(pager, queryWrapper);

            if(!CollectionUtils.isEmpty(result.getRecords())){
                List<ImMessagePO> msgs = result.getRecords();
                Collections.sort(msgs, Comparator.comparingLong(ImMessagePO::getId));
                response.setMsg(result.getRecords());
                response.setTotal(result.getTotal());
            }

        }else {
            //peer to peer
            Integer offset = (page - 1) * pageSize;
            MessageQueryDto dto = MessageQueryDto.builder().fromUserId(fromUserId).topicId(topicId)
                    .toUserId(toUserId).page(page).pageSize(pageSize).offset(offset).build();
            List<ImMessagePO> msgs = messageMapper.getSingleChatMessageList(dto);
            Integer totalCount = messageMapper.toalCountOfSingleChatMessageList(dto);
            Collections.sort(msgs, Comparator.comparingLong(ImMessagePO::getId));
            response.setMsg(msgs);
            response.setTotal(totalCount);
        }

        return response;

    }
}
