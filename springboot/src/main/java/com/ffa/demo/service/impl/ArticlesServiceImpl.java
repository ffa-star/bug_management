package com.ffa.demo.service.impl;

import com.ffa.demo.entity.Articles;
import com.ffa.demo.mapper.ArticlesMapper;
import com.ffa.demo.service.IArticlesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ffa
 * @since 2023-11-13
 */
@Service
public class ArticlesServiceImpl extends ServiceImpl<ArticlesMapper, Articles> implements IArticlesService {

}
