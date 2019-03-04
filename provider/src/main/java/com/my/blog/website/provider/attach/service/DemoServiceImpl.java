package com.my.blog.website.provider.attach.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.my.blog.website.DemoService;

@Service
public class DemoServiceImpl implements DemoService {

    public String sayHello(String name) {
        return "Hello, " + name + " (from Spring Boot)";
    }

}