package com.ffa.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author ffa
 * @since 2023-11-22
 */
@Getter
@Setter
  @ApiModel(value = "Comment对象", description = "")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("最上级评论id")
      private Integer originId;

      @ApiModelProperty("评论人id")
      private Integer userId;

    private String content;

    private LocalDateTime createTime;

      @ApiModelProperty("父id")
      private Integer pid;

    private Integer articleId;

    @TableField(exist = false)  //不再数据库中
    private String username;


}
