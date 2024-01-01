package com.leo.springbootstart.model.dto.user;

import com.leo.springbootstart.common.PageRequest;
import lombok.Data;

@Data
public class UserQueryRequest extends PageRequest {
    private long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户角色：user / admin
     */
    private String userRole;
}
