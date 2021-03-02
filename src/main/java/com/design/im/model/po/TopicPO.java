package com.design.im.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author mybatis-plus
 * @since 2021-02-28
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("Topic")
public class TopicPO implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Long id;

    private String name;

    private Long topicmaster;


}
