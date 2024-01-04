package com.leo.springbootstart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leo.springbootstart.model.entity.Post;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author brianxie
 * @description 针对表【post(帖子)】的数据库操作Mapper
 * @createDate 2024-01-04 12:11:22
 * @Entity com.leo.springbootstart.model.entity.Post
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {

}




