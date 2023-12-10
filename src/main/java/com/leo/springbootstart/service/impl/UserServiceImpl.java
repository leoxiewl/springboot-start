package com.leo.springbootstart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.springbootstart.model.entity.User;
import com.leo.springbootstart.service.UserService;
import com.leo.springbootstart.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author brianxie
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-12-10 23:03:39
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




