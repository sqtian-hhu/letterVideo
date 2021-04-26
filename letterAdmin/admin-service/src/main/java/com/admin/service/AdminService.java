package com.admin.service;

import com.admin.pojo.Admin;

import java.util.List;

public interface AdminService {


    /**
     * 查找所有用户
     * @return
     */
    List<Admin> findAll();

    /**
     * 保存新用户信息
     * @param admin
     */
    void saveAdmin(Admin admin);

    Admin findByName(String username);

    /**
     * 查找登录用户是否存在
     * @return
     */

    Admin queryForLogin(String username, String password);


    boolean queryUsernameIsExist(String username);

}
