package com.ffa.demo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ffa.demo.common.Constants;
import com.ffa.demo.controller.dto.UserDto;
import com.ffa.demo.entity.User;
import com.ffa.demo.exception.ServiceException;
import com.ffa.demo.mapper.UserMapper;
import com.ffa.demo.service.IUserService;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private static final Log LOG=Log.get();
    @Override
    public User login(UserDto userdto) {

        User user = getuserinfo(userdto);
        if(user!=null){

            return user;
        }else{
            throw new ServiceException(Constants.CODE_600,"用户名或密码错误");
        }

    }

    @Override
    public User register(UserDto userDto) {
        User user = getuserinfo(userDto);

        if (user == null) {
            user = new User();
            BeanUtil.copyProperties(userDto,user,true);
            save(user);
        }else{
            throw new ServiceException(Constants.CODE_600,"用户已存在");
        }
        return user;
    }


//    public List<ArticleUser> fromArticleToUser(List<Integer> userId) {
//        QueryWrapper<ArticleUser> queryWrapper = new QueryWrapper<>();
//        queryWrapper.
//        return null;
//    }

    public User getuserinfo(UserDto userdto) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userdto.getUsername());
        queryWrapper.eq("password", userdto.getPassword());
        User user;
        try {
            user = getOne(queryWrapper);
            System.out.println("======" + user);

        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        return user;

    }


}
