package com.my.blog.website.consummer.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.my.blog.website.AttachService;
import com.my.blog.website.DemoService;
//import com.imooc.springboot.dubbo.demo.DemoService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoConsumerController {

    @Reference(check = false)
    private DemoService demoService;

    @Reference(check = false)
    private AttachService attachService;
    @RequestMapping("/hello")
    public int sayHello() {
        return attachService.selectCount(new EntityWrapper());
    }

}