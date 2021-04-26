package com.admin.mapper;

import com.admin.pojo.Admin;
import com.admin.utils.MyMapper;
import org.apache.ibatis.annotations.Select;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface adminMapper extends MyMapper<Admin> {

    /**
     * 使用了tk.mybatis插件后这些单表查询的方法都不需要自己写了
     * service里面调用的mapper可以直接提供方法
     * @return
     */
//    @Select("select * from admin_users")
    List<Admin> findAll();

    void saveAdmin(Admin admin);

    Admin findByName(String username);

}
