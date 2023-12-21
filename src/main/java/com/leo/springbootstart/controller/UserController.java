package com.leo.springbootstart.controller;

import com.leo.springbootstart.common.ApiCode;
import com.leo.springbootstart.common.DeleteRequest;
import com.leo.springbootstart.common.R;
import com.leo.springbootstart.model.dto.user.UserAddRequest;
import com.leo.springbootstart.model.entity.User;
import com.leo.springbootstart.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * admin 添加用户
     *
     * @param userAddRequest
     * @return
     */
    @PostMapping("/add")
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

    /**
     * admin 删除用户
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public R<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null) {
            return R.failed(ApiCode.FAILED.getCode(), "参数错误");
        }
        long id = deleteRequest.getId();
        if (id <= 0) {
            return R.failed(ApiCode.FAILED.getCode(), "参数错误");
        }
        boolean result = userService.removeById(id);
        if (!result) {
            return R.failed(ApiCode.FAILED.getCode(), "删除失败");
        }
        return R.success(true);
    }
}
