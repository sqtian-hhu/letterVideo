package com.admin.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;


import javax.persistence.Id;

//@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})

@Table(name = "admin_users")
public class Admin {

    /**
     * 使用了tk.mybatis插件后
     * 在xxx.xxx.xxx.pojo包中，进行数据库表和类的映射
     * （对于数据库表中不存在的变量要用@Transient注解进行忽略映射，否则会报在数据库表找不到对应字段的错误）
     */

    @Id
    private Integer id;

    @Column(name = "usertoken")
    private String usertoken;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;


    public Admin() {
    }

    public Admin(String usertoken, String username, String password) {
        this.usertoken = usertoken;
        this.username = username;
        this.password = password;
    }


    /**
     *处理json对象的类，数据必须要有相关的get和set方法
     *
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsertoken() {
        return usertoken;
    }

    public void setUsertoken(String usertoken) {
        this.usertoken = usertoken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", usertoken='" + usertoken + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }




}
