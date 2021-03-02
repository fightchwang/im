package com.design.im.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author mybatis-plus
 * @since 2021-03-01
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("IM_Message")
public class ImMessagePO implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Long id;

    @TableField("toUserId")
    private Long toUserId;

    @TableField("fromUserId")
    private Long fromUserId;

    @TableField("topicId")
    private Long topicId;

    private Boolean isgroupmessage;

    private String msgcontent;

    private Long time;


}
