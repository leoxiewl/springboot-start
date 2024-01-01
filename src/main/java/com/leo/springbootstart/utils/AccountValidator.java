package com.leo.springbootstart.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountValidator {

    public static boolean isValidAccount(String account) {
        // 定义一个正则表达式，匹配不包含特殊字符的账号（只允许字母、数字和下划线）
        String regex = "^[a-zA-Z0-9_]+$";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 创建 Matcher 对象
        Matcher matcher = pattern.matcher(account);

        // 进行匹配并返回结果
        return matcher.matches();
    }
}
