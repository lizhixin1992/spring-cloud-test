package com.lzx.cloud.client.controller;

import com.lzx.cloud.client.entity.User;
import com.lzx.cloud.client.feign.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class UserController {
    @Autowired
    private UserFeignClient userFeignClient;

    @GetMapping("/user/{id}")
    public User findById(@PathVariable Long id) {
        User user = this.userFeignClient.findById(id);
        return user;
    }

    @GetMapping("/user/all")
    public List<User> saveUser(){
        return this.userFeignClient.saveUser();
    }
}