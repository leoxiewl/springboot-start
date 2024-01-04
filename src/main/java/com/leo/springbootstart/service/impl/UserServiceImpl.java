package com.leo.springbootstart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.springbootstart.common.ErrorCode;
import com.leo.springbootstart.constant.CommonConstant;
import com.leo.springbootstart.exception.BusinessException;
import com.leo.springbootstart.mapper.UserMapper;
import com.leo.springbootstart.model.dto.user.UserQueryRequest;
import com.leo.springbootstart.model.entity.User;
import com.leo.springbootstart.model.enums.UserRoleEnum;
import com.leo.springbootstart.model.vo.LoginUserVO;
import com.leo.springbootstart.model.vo.UserVO;
import com.leo.springbootstart.service.UserService;
import com.leo.springbootstart.utils.AccountValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.leo.springbootstart.constant.CommonConstant.USER_LOGIN_STATE;

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

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userAccount.length() < 4 || !AccountValidator.isValidAccount(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "账号格式错误");
        }
        if (userPassword.length() < 8 || userPassword.length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "密码格式错误");
        }
        String encryptPassword = DigestUtils.md5DigestAsHex((CommonConstant.SALT + userPassword).getBytes());

        User user = userMapper.selectOne(new QueryWrapper<User>().eq("user_account", userAccount));
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        String originPassword = user.getUserPassword();
        if (!encryptPassword.equals(originPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);

        request.getSession().setAttribute(USER_LOGIN_STATE, loginUserVO);

        return loginUserVO;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    // TODO 坑 因为有缓存，修改权限后不能及时更新
    @Override
    public LoginUserVO getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        LoginUserVO currentUser = (LoginUserVO) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
//        long userId = currentUser.getId();
//        User byId = this.getById(userId);
//        if (currentUser == null) {
//            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
//        }
        return currentUser;
    }

    @Override
    public LoginUserVO getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        LoginUserVO currentUser = (LoginUserVO) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        LoginUserVO loginUserVO = new LoginUserVO();
        User user = this.getById(currentUser.getId());
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {

        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }


}




