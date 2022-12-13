package com.cactus.qqcommon;

import java.io.Serializable;

/**
 * Created by cactusli on 2022/11/11 14:06
 * 表示一个用户/客户信心
 */
public class User implements Serializable {

    private static final long serialVersionUID = 420L;

    private String userId; //用户Id/用户名
    private String password; //用户密码

    public User() {
    }

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
