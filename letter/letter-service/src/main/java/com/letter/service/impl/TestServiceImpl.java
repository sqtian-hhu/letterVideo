package com.letter.service.impl;

import com.letter.dao.TestDao;
import com.letter.pojo.Users;
import com.letter.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sqtian
 * @create 2020-08-08-14:19
 */
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestDao testDao;

    public List<Users> findAll() {
        System.out.println("Service-查询所有");
        return testDao.findAll();

    }
}
