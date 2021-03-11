package com.design.im.dao;

import com.design.im.model.MessageQueryDto;
import com.design.im.model.po.ImMessagePO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mybatis-plus
 * @since 2021-03-01
 */
public interface ImMessageMapper extends BaseMapper<ImMessagePO> {

    List<ImMessagePO> getSingleChatMessageList(@Param("param")MessageQueryDto dto);
    Integer toalCountOfSingleChatMessageList(@Param("param")MessageQueryDto dto);

}
