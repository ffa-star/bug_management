package com.ffa.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
 * @since 2023-11-14
 */
@Getter
@Setter
  @ApiModel(value = "Resources对象", description = "")
public class Resources implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

    private Integer articleId;

    private String resourceType;

    private String resourceUrl;

    private Double size;

    private String name;


}
