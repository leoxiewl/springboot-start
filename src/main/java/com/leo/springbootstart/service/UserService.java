package com.leo.springbootstart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.springbootstart.model.dto.user.UserQueryRequest;
import com.leo.springbootstart.model.entity.User;
import com.leo.springbootstart.model.vo.LoginUserVO;
import com.leo.springbootstart.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author brianxie
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2023-12-10 23:03:39
 */
public interface UserService extends IService<User> {
    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    List<UserVO> transferUserVOList(List<User> userList);

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO transferUserVO(User user);

    Long register(String userAccount, String userPassword, String checkPassword);

    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    boolean userLogout(HttpServletRequest request);

    LoginUserVO getLoginUser(HttpServletRequest request);

}
