package com.letter.dao;

import com.letter.pojo.Users;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author sqtian
 * @create 2020-08-08-14:17
 */

@Component
public interface TestDao {

//    @Select("select * from users")
    List<Users> findAll();
}
