package com.ffa.demo.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//接收前端传来的用户信息
public class UserDto {
    private String username;
    private String password;
    private String nickname;
    private String avatar;
}
