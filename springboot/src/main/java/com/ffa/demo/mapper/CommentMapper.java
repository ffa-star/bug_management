package com.ffa.demo.mapper;

import com.ffa.demo.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ffa
 * @since 2023-11-22
 */
public interface CommentMapper extends BaseMapper<Comment> {

@Select("select c.*,u.username from comment c left join sys_user u on c.user_id=u.id where c.article_id = #{articleId}")
    List<Comment> findCommentDetail(@Param("articleId") Integer articleId);
}
