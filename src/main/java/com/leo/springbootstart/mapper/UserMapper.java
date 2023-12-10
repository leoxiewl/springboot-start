package com.leo.springbootstart.mapper;

import com.leo.springbootstart.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author brianxie
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2023-12-10 23:03:39
* @Entity com.leo.springbootstart.model.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




