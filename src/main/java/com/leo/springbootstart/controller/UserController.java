package com.leo.springbootstart.controller;

import com.leo.springbootstart.common.R;
import com.leo.springbootstart.model.dto.UserAddRequest;
import com.leo.springbootstart.model.entity.User;
import com.leo.springbootstart.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @RequestMapping("/add")
    public R<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean result = userService.save(user);
        return R.success(user.getId());
    }
}
