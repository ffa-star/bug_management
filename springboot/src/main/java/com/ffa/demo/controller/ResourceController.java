package com.ffa.demo.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ffa.demo.controller.dto.ResourcesDto;
import com.ffa.demo.entity.Resources;
import com.ffa.demo.mapper.ResourcesMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/resources")

public class ResourceController {
    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Resource
    private ResourcesMapper resourcesMapper;


    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        String orginFilename=file.getOriginalFilename();
        String resourceType = FileUtil.extName(orginFilename);
        double size = file.getSize();
        //先存储到磁盘
        File uploadParentFile = new File(fileUploadPath);
        if(!uploadParentFile.exists()){
            uploadParentFile.mkdirs();
        }
        //定义一个文件唯一的标识码
        String uuid = IdUtil.fastSimpleUUID();
        File uploadFile = new File(fileUploadPath+uuid+ StrUtil.DOT+resourceType);
        file.transferTo(uploadFile);
        Resources rs = new Resources();
        rs.setResourceType(resourceType);
        rs.setSize(size);
        rs.setName(orginFilename);
        resourcesMapper.insert(rs);
        String url = "http://localhost:9090"+uuid+StrUtil.DOT+resourceType;
        rs.setResourceUrl(url);

        return url;
        //存储数据库
    }

    //获取tags
    //调用mapper方法
    @GetMapping("/{id}")
    public Map<String,Integer>  getTags(@PathVariable Long id){
        ResourcesDto resdtoList = resourcesMapper.getUserTags(id);
        String[] tagsArray = resdtoList.getTags().split(",");
        // 统计每个元素出现的次数
        Map<String, Integer> tagCountMap = new HashMap<>();
        for (String tag : tagsArray) {
            // 去除首尾空格
            tag = tag.trim();

            // 忽略空字符串
            if (!tag.isEmpty()) {
                tagCountMap.put(tag, tagCountMap.getOrDefault(tag, 0) + 1);
            }
        }
        Map<String, Integer> sortedTagCountMap = sortByValue(tagCountMap);
        // 返回统计结果
        return sortedTagCountMap;
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
