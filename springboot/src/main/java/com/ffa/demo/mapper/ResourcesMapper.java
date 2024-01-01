package com.ffa.demo.mapper;

import com.ffa.demo.controller.dto.ResourcesDto;
import com.ffa.demo.entity.Resources;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ffa
 * @since 2023-11-14
 */
public interface ResourcesMapper extends BaseMapper<Resources> {
    ResourcesDto getUserTags(Long userid);
}
