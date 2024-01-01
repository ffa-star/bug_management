package com.ffa.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @since 2023-11-13
 */
@Getter
@Setter
  @ApiModel(value = "Articles对象", description = "")
public class Articles implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

    private Integer userId;

    private String title;

    private String content;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String tag;

}
