package com.letter.service.impl;

import com.letter.dao.UsersFansMapper;
import com.letter.dao.UsersLikeVideosMapper;
import com.letter.dao.UsersMapper;
import com.letter.dao.UsersReportMapper;
import com.letter.pojo.Users;
import com.letter.pojo.UsersFans;
import com.letter.pojo.UsersLikeVideos;
import com.letter.pojo.UsersReport;
import com.letter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author sqtian
 * @create 2020-08-08-20:16
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper userMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private UsersReportMapper usersReportMapper;

    @Autowired
    private Sid sid;

    //查询一般使用SUPPORTS事务
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryUsernameIsExist(String username) {

        Users user = new Users();
        user.setUsername(username);
        Users result = userMapper.selectOne(user);

        return !(result == null);
    }

    //增删改的事务使用REQUIRED
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(Users user) {


        //利用idworker工具包 生成全局唯一的id
        String userId = sid.nextShort();
        user.setId(userId);
        userMapper.insert(user);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserForLogin(String username, String password) {

        //通过example查找账户密码都匹配的对象
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();

        //criteria 标准 andEqualTo 同时满足
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);
        Users result = userMapper.selectOneByExample(userExample);

        return result;


    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserInfo(Users user) {

        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", user.getId());
        //updateByExampleSelective哪个属性有值就去更新哪个属性, updateByExample是所有属性都更新
        userMapper.updateByExampleSelective(user,userExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserInfo(String userId) {

        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", userId);
        return userMapper.selectOneByExample(userExample);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isUserLikeVideo(String loginUserId, String videoId) {

        if (StringUtils.isBlank(loginUserId) || StringUtils.isBlank(videoId)) {
            return false;
        }

        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();

//        andEqualTo同时满足userId=loginUserId和videoId=videoId
        criteria.andEqualTo("userId",loginUserId);
        criteria.andEqualTo("videoId",videoId);

        List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(example);


        if (list != null && list.size() >0) {
            return true;
        }

        return false;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryIfFollow(String userId, String fanId) {
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);

        List<UsersFans> list = usersFansMapper.selectByExample(example);

        if(list != null && !list.isEmpty() && list.size() > 0) {
            return true;
        }


        return false;

//        return list != null && !list.isEmpty();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUserFanRelation(String userId, String fanId) {
        String relID = sid.nextShort();

        UsersFans userFan = new UsersFans();
        userFan.setFanId(fanId);
        userFan.setId(relID);
        userFan.setUserId(userId);

        usersFansMapper.insert(userFan);

        //累加粉丝数
        userMapper.addFansCount(userId);
        userMapper.addFollersCount(fanId);


    }

    public void deleteUserFanRelation(String userId, String fanId) {
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);

        usersFansMapper.deleteByExample(example);

        userMapper.reduceFansCount(userId);
        userMapper.reduceFollersCount(fanId);

    }

    public void updateReport(UsersReport usersReport) {

        usersReport.setId(sid.nextShort());
        usersReport.setCreateDate(new Date());
        usersReportMapper.insert(usersReport);

    }
}
