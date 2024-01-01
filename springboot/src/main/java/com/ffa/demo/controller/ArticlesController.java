package com.ffa.demo.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ffa.demo.controller.dto.ArticleUserDto;
import com.ffa.demo.mapper.ArticleUserMapper;
import com.ffa.demo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

import com.ffa.demo.service.IArticlesService;
import com.ffa.demo.entity.Articles;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ffa
 * @since 2023-11-13
 */
@RestController
@RequestMapping("/articles")
public class ArticlesController {
    @Resource
    private IArticlesService articlesService;
    @Resource
    private IUserService userService;
    @Autowired
    private ArticleUserMapper articleUserMapper;

      @PostMapping("/save")
      public boolean saveArticles(@RequestBody Articles articles){
          return articlesService.saveOrUpdate(articles);
      }

      @DeleteMapping("/{id}")
      public Boolean delete(@PathVariable Integer id){
           return articlesService.removeById(id);
      }

      @GetMapping
          public List<ArticleUserDto> findAll() {

          return this.articleUserMapper.selectAll();
//          List<Integer> l = articles.stream()
//                  .map(Articles::getUserId)
//                  .collect(Collectors.toList());

          }

        @GetMapping("/{id}")
          public Articles findOne(@PathVariable Integer id) {
              return articlesService.getById(id);
          }
        @PostMapping("/del/batch")
            public boolean deleteBatch(@RequestBody List<Integer> ids){
                return articlesService.removeByIds(ids);
            }

        @GetMapping("/page")
        public Page<ArticleUserDto> findPage(@RequestParam Integer pageNum,
                                @RequestParam Integer pageSize,@RequestParam String name) {

           QueryWrapper<ArticleUserDto> wrapper = new QueryWrapper<>();
            if(StrUtil.isNotBlank(name)){
                wrapper.like("username",name);
            }
            wrapper.apply("articles.user_id=sys_user.id");
           wrapper.orderByDesc("id");

           Page<ArticleUserDto> page = new Page<>(pageNum,pageSize);
        return articleUserMapper.selectByLimitation(page,wrapper);
    }


}

