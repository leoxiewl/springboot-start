package com.leo.springbootstart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.springbootstart.mapper.UserMapper;
import com.leo.springbootstart.model.dto.user.UserQueryRequest;
import com.leo.springbootstart.model.entity.User;
import com.leo.springbootstart.model.vo.UserVO;
import com.leo.springbootstart.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author brianxie
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2023-12-10 23:03:39
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (userQueryRequest == null) {
            // 不抛出异常，请求参数为空直接返回空的 queryWrapper
            return queryWrapper;
            // throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "参数错误");
        }

        long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userRole = userQueryRequest.getUserRole();
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        if (id > 0) {
            queryWrapper.eq("id", id);
        }
        if (userName != null && !userName.isEmpty()) {
            queryWrapper.like("user_name", userName);
        }
        if (userAccount != null && !userAccount.isEmpty()) {
            queryWrapper.like("user_account", userAccount);
        }
        if (userRole != null && !userRole.isEmpty()) {
            queryWrapper.like("user_role", userRole);
        }
        queryWrapper.last("limit " + (current - 1) * pageSize + "," + pageSize);
        return queryWrapper;
    }

    @Override
    public List<UserVO> transferUserVOList(List<User> userList) {
        // 输入为空，返回也为空
        if (userList == null || userList.isEmpty()) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::transferUserVO).collect(Collectors.toList());

    }

    @Override
    public UserVO transferUserVO(User user) {
        if (user == null) {
            return new UserVO();
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}




