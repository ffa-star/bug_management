package com.ffa.demo.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArticleUserDto {

    private Integer id;

    private Integer userId;

    private String title;

    private String content;

    private String username;

    private String tag;

    private LocalDateTime updateTime;

}
