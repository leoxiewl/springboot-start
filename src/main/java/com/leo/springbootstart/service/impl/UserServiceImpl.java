package com.leo.springbootstart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.springbootstart.common.ErrorCode;
import com.leo.springbootstart.constant.CommonConstant;
import com.leo.springbootstart.exception.BusinessException;
import com.leo.springbootstart.mapper.UserMapper;
import com.leo.springbootstart.model.dto.user.UserQueryRequest;
import com.leo.springbootstart.model.entity.User;
import com.leo.springbootstart.model.vo.UserVO;
import com.leo.springbootstart.service.UserService;
import com.leo.springbootstart.utils.AccountValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
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

    @Resource
    private UserMapper userMapper;

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

    @Override
    public Long register(String userAccount, String userPassword, String checkPassword) {
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "参数错误");
        }
        if (userAccount.length() < 4 || !AccountValidator.isValidAccount(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "账号格式错误");
        }
        if (userPassword.length() < 8 || userPassword.length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "密码格式错误");
        }
        if (checkPassword.length() < 8 || checkPassword.length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "校验密码格式错误");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "输入密码不一致");
        }

        synchronized (userAccount.intern()) {
            User user1 = userMapper.selectOne(new QueryWrapper<User>().eq("user_account", userAccount));
            if (user1 != null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "账号已存在");
            }

            String encryptPassword = DigestUtils.md5DigestAsHex((CommonConstant.SALT + userPassword).getBytes());

            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserName(userAccount);
            user.setUserPassword(encryptPassword);
            int count = userMapper.insert(user);
            if (count <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "注册失败");
            }
            return user.getId();
        }
    }
}




