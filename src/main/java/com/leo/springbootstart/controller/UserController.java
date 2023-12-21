package com.leo.springbootstart.controller;

import com.leo.springbootstart.common.ApiCode;
import com.leo.springbootstart.common.DeleteRequest;
import com.leo.springbootstart.common.R;
import com.leo.springbootstart.model.dto.user.UserAddRequest;
import com.leo.springbootstart.model.dto.user.UserUpdateRequest;
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

    /**
     * admin 更新用户
     *
     * @param userUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public R<Long> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null) {
            return R.failed(ApiCode.FAILED.getCode(), "参数错误");
        }
        
        long id = userUpdateRequest.getId();
        String userName = userUpdateRequest.getUserName();
        String userAccount = userUpdateRequest.getUserAccount();
        String userPassword = userUpdateRequest.getUserPassword();
        String userAvatar = userUpdateRequest.getUserAvatar();
        Integer gender = userUpdateRequest.getGender();
        String userRole = userUpdateRequest.getUserRole();
        // 传入参数不为空才加入到 update 中
        User user = new User();
        if (id > 0) {
            user.setId(id);
        }
        if (userName != null && !userName.isEmpty()) {
            user.setUserName(userName);
        }
        if (userAccount != null && !userAccount.isEmpty()) {
            user.setUserAccount(userAccount);
        }
        if (userPassword != null && !userPassword.isEmpty()) {
            user.setUserPassword(userPassword);
        }
        if (userAvatar != null && !userAvatar.isEmpty()) {
            user.setUserAvatar(userAvatar);
        }
        if (gender >= 0) {
            user.setGender(gender);
        }
        if (userRole != null && !userRole.isEmpty()) {
            user.setUserRole(userRole);
        }

        // 判断要修改的用户是否存在
        User userExist = userService.getById(id);
        if (userExist == null) {
            return R.failed(ApiCode.FAILED.getCode(), "用户不存在");
        }

        boolean result = userService.updateById(user);
        if (!result) {
            return R.failed(ApiCode.FAILED.getCode(), "更新失败");
        }
        return R.success(user.getId());
    }
}
