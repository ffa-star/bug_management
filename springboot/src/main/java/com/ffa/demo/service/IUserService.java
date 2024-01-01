package com.ffa.demo.service;

import com.ffa.demo.controller.dto.UserDto;
import com.ffa.demo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ffa
 * @since 2023-11-08
 */
public interface IUserService extends IService<User> {

    User login(UserDto userdto);

    User register(UserDto userDto);

//    List<ArticleUser> fromArticleToUser(List<Integer> userId);
}
