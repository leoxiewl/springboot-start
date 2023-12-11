package com.leo.springbootstart.controller;

import com.leo.springbootstart.common.ApiCode;
import com.leo.springbootstart.common.R;
import com.leo.springbootstart.model.dto.UserAddRequest;
import com.leo.springbootstart.model.entity.User;
import com.leo.springbootstart.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping ("/add")
    public R<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        if (userAddRequest == null) {
            return R.failed(ApiCode.FAILED.getCode(), "参数错误");
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean result = userService.save(user);
        if (!result) {
            return R.failed(ApiCode.FAILED.getCode(), "添加失败");
        }
        return R.success(user.getId());
    }
}
