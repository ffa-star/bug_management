package com.ffa.demo.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ffa.demo.common.Result;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import com.ffa.demo.service.ICommentService;
import com.ffa.demo.entity.Comment;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ffa
 * @since 2023-11-22
 */
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Resource
    private ICommentService commentService;

      @PostMapping("/save")
      public Result saveUser(@RequestBody Comment comment){
          return commentService.saveOrUpdate(comment) ? Result.success():Result.error();
      }

      @DeleteMapping("/{id}")
      public Boolean delete(@PathVariable Integer id){
           return commentService.removeById(id);
      }

      @GetMapping
          public List<Comment> findAll() {
              return commentService.list();
          }

        @GetMapping("/{id}")
          public Comment findOne(@PathVariable Integer id) {
              return commentService.getById(id);
          }
        @PostMapping("/del/batch")
            public boolean deleteBatch(@RequestBody List<Integer> ids){
                return commentService.removeByIds(ids);
            }

        @GetMapping("/tree/{articleId}")
        public Result findTree(@PathVariable Integer articleId) {

           List<Comment> articleComments = commentService.findCommentDetail(articleId);

        return Result.success(articleComments);
    }


}

