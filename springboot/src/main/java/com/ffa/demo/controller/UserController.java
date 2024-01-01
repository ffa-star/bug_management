package com.ffa.demo.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ffa.demo.common.Constants;
import com.ffa.demo.common.Result;
import com.ffa.demo.controller.dto.UserDto;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;
import com.ffa.demo.service.IUserService;
import com.ffa.demo.entity.User;

import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ffa
 * @since 2023-11-08
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private IUserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody UserDto userDto){
        String username = userDto.getUsername();
        String password =userDto.getPassword();
        if(StrUtil.isBlank(username)||StrUtil.isBlank(password)){
            return Result.error(Constants.CODE_400,"参数错误");
        }
        User user =  userService.login(userDto);
        return Result.success(user);

    }
    @PostMapping("/register")
    public Result register(@RequestBody UserDto userDto){
        String username = userDto.getUsername();
        String password =userDto.getPassword();
        if(StrUtil.isBlank(username)||StrUtil.isBlank(password)){
            return Result.error(Constants.CODE_400,"参数错误");
        }
         User user = userService.register(userDto);
        return Result.success(user);

    }

        @GetMapping("/export")
        public void export(HttpServletResponse response) throws Exception{
            List<User> list = userService.list();
            //通过工具类创建writer 写出到磁盘路径
            //在内存操作，写出到浏览器
            ExcelWriter writer = ExcelUtil.getWriter(true);
            //自定义标题别名
            writer.addHeaderAlias("username","用户名");
            writer.addHeaderAlias("password","密码");
            writer.addHeaderAlias("nickname","昵称");
            writer.addHeaderAlias("email","邮箱");
            writer.addHeaderAlias("phone","手机");
            writer.addHeaderAlias("address","地址");
            writer.addHeaderAlias("createTime","创建时间");
            writer.addHeaderAlias("avatarUrl","头像");

            //一次性写出list内的对象到Excel，使用默认样式，强制输出标题
            writer.write(list,true);

            //设置浏览器响应的格式
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            String fileName = URLEncoder.encode("用户信息","UTF-8");
            response.setHeader("Content-Disposition","attachment;filename="+fileName+".xlsx");

            ServletOutputStream out = response.getOutputStream();
            writer.flush(out,true);
            out.close();
            writer.close();


        }
      @PostMapping("/save")
      public boolean saveUser(@RequestBody User user){
          return userService.saveOrUpdate(user);
      }

      @DeleteMapping("/{id}")
      public Boolean delete(@PathVariable Integer id){
           return userService.removeById(id);
      }

      @GetMapping
          public List<User> findAll() {
              return userService.list();
          }

        @GetMapping("/{id}")
          public User findOne(@PathVariable Integer id) {
              return userService.getById(id);
          }
    @PostMapping("/del/batch")
    public boolean deleteBatch(@RequestBody List<Integer> ids){
        return userService.removeByIds(ids);
    }
    @GetMapping("/page")
    //mybatis-plus分页查询
    public IPage<User> findPage(@RequestParam Integer pageNum,
                                @RequestParam Integer pageSize,
                                @RequestParam(defaultValue = "") String username,
                                @RequestParam(defaultValue = "") String email,
                                @RequestParam(defaultValue = "") String address){
        IPage<User> page = new Page<>(pageNum,pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(!"".equals(username)){
            queryWrapper.like("username",username);
        }
        if(!"".equals(email)){
            queryWrapper.like("email",email);
        }
        if(!"".equals(address)){
            queryWrapper.like("address",address);
        }
        queryWrapper.orderByDesc("id");

        return userService.page(page,queryWrapper);
    }
}


