package com.ffa.demo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ffa.demo.controller.dto.ArticleUserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Wrapper;
import java.util.List;

@Mapper
public interface ArticleUserMapper extends BaseMapper<ArticleUserDto> {
    @Select("select articles.*,sys_user.username from articles,sys_user where articles.user_id=sys_user.id")
    List<ArticleUserDto> selectAll();

    @Select("select articles.*,sys_user.username from articles,sys_user ${ew.customSqlSegment}")
    Page<ArticleUserDto> selectByLimitation(Page<ArticleUserDto> page, @Param("ew") QueryWrapper<ArticleUserDto> queryWrapper);
}
