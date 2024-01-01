package com.ffa.demo.service.impl;

import com.ffa.demo.entity.Articles;
import com.ffa.demo.mapper.ArticleMapper;
import com.ffa.demo.service.IArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ffa
 * @since 2023-11-08
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Articles> implements IArticleService {

}
