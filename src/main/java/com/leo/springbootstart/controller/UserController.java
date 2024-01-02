package com.leo.springbootstart.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leo.springbootstart.common.ApiCode;
import com.leo.springbootstart.common.DeleteRequest;
import com.leo.springbootstart.common.ErrorCode;
import com.leo.springbootstart.common.R;
import com.leo.springbootstart.exception.BusinessException;
import com.leo.springbootstart.model.dto.user.*;
import com.leo.springbootstart.model.entity.User;
import com.leo.springbootstart.model.vo.LoginUserVO;
import com.leo.springbootstart.model.vo.UserVO;
import com.leo.springbootstart.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public R<Long> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return R.failed(ApiCode.FAILED.getCode(), "参数错误");
        }

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return R.failed(ApiCode.FAILED.getCode(), "参数错误");
        }

        Long userId = userService.register(userAccount, userPassword, checkPassword);
        if (userId <= 0) {
            return R.failed(ApiCode.FAILED.getCode(), "注册失败");
        }
        return R.success(userId);
    }

    @PostMapping("/login")
    public R<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return R.success(loginUserVO);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return R.success(result);
    }

    /**
     * 通过 id 获取 userVO 类
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public R<UserVO> getUserVOById(long id) {
        if (id <= 0) {
            return R.failed(ApiCode.FAILED.getCode(), "参数错误");
        }
        UserVO userVO = new UserVO();
        User user = userService.getById(id);
        if (user == null) {
            return R.failed(ApiCode.FAILED.getCode(), "用户不存在");
        }
        BeanUtils.copyProperties(user, userVO);
        return R.success(userVO);
    }

    @PostMapping("/list/page/vo")
    public R<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        long current = userQueryRequest.getCurrent();
        if (current <= 0) {
            current = 1;
            userQueryRequest.setCurrent(current);
        }
        long pageSize = userQueryRequest.getPageSize();
        if (pageSize <= 0) {
            pageSize = 10;
            userQueryRequest.setPageSize(pageSize);
        }
        // 获取用户信息
        Page<User> page = userService.page(new Page<>(userQueryRequest.getCurrent(), userQueryRequest.getPageSize()),
                userService.getQueryWrapper(userQueryRequest));
        // 把 User 信息转化为 UserVO 信息
        List<UserVO> userVOS = userService.transferUserVOList(page.getRecords());
        Page<UserVO> userVOPage = new Page<>();
        userVOPage.setRecords(userVOS);
        return R.success(userVOPage);
    }

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
     * 更新用户
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

        // 没有 id 不能更新
        boolean result = userService.updateById(user);
        if (!result) {
            return R.failed(ApiCode.FAILED.getCode(), "更新失败");
        }
        return R.success(user.getId());
    }

    /**
     * admin 根据 id 获取用户详情
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public R<User> getUserById(long id) {
        if (id <= 0) {
            return R.failed(ApiCode.FAILED.getCode(), "参数错误");
        }
        User user = userService.getById(id);
        if (user == null) {
            return R.failed(ApiCode.FAILED.getCode(), "用户不存在");
        }
        return R.success(user);
    }

    /**
     * admin 分页获取用户列表
     *
     * @return
     */
    @PostMapping("/list/page")
    public R<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest) {
        long current = userQueryRequest.getCurrent();
        if (current <= 0) {
            current = 1;
            userQueryRequest.setCurrent(current);
        }
        long pageSize = userQueryRequest.getPageSize();
        if (pageSize <= 0) {
            pageSize = 10;
            userQueryRequest.setPageSize(pageSize);
        }
        Page<User> userPage = userService.page(new Page<>(current, pageSize), userService.getQueryWrapper(userQueryRequest));
        return R.success(userPage);
    }
}
