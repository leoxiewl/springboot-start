package com.leo.springbootstart.model.dto.user;

import lombok.Data;

@Data
public class UserLoginRequest {

    private String userAccount;
    private String userPassword;
}
