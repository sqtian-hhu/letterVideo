package com.admin.service.impl;

import com.admin.mapper.adminMapper;
import com.admin.pojo.Admin;
import com.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private adminMapper adminMapper;

    @Override
    public List<Admin> findAll() {
        return adminMapper.selectAll();
    }

    @Override
    public void saveAdmin(Admin admin) {
//        adminMapper.insert(admin);
        adminMapper.saveAdmin(admin);
    }

    @Override
    public Admin findByName(String username) {
        return adminMapper.findByName(username);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Admin queryForLogin(String username, String password) {
        //通过example查找账户密码都匹配的对象
        //想用Example需要导入tk.mybatis依赖
        Example adminExample = new Example(Admin.class);
        Example.Criteria criteria = adminExample.createCriteria();

        //criteria 标准 andEqualTo 同时满足
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);

        return adminMapper.selectOneByExample(adminExample);

    }

    @Override
    public boolean queryUsernameIsExist(String username) {

        return adminMapper.findByName(username) != null;
    }
}
