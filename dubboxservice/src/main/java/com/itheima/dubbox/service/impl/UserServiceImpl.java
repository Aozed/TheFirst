package com.itheima.dubbox.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dubbox.service.UserService;

/**
 * 实现类
 */
@Service    //必须使用dubbox的注解
public class UserServiceImpl implements UserService{
    public String getName() {
        return "itheima";
    }
}
