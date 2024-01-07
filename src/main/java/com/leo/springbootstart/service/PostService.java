package com.leo.springbootstart.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.springbootstart.model.dto.post.PostEditRequest;
import com.leo.springbootstart.model.dto.post.PostQueryRequest;
import com.leo.springbootstart.model.entity.Post;
import com.leo.springbootstart.model.vo.PostVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author brianxie
 * @description 针对表【post(帖子)】的数据库操作Service
 * @createDate 2024-01-04 12:11:22
 */
public interface PostService extends IService<Post> {

    /**
     * 校验帖子
     *
     * @param post
     * @param add
     */
    void validPost(Post post, boolean add);

    PostVO getPostVO(Post post, HttpServletRequest request);

    QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest);

    Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request);

    boolean editPost(PostEditRequest postEditRequest, HttpServletRequest request);
}
