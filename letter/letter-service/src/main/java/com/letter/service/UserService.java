package com.letter.service;

import com.letter.pojo.Users;
import com.letter.pojo.UsersReport;

public interface UserService {


	/**
	 * 判断用户名是否存在
	 * @param username
	 * @return
	 */
	public boolean queryUsernameIsExist(String username);


	/**
	 * 保存注册用户信息
	 * @param user
	 */
	public void saveUser(Users user);

	/**
	 * 用于用户登录,根据用户名和密码查询用户
	 * @param username
	 * @param password
	 * @return
	 */
	public Users queryUserForLogin(String username,String password);


	/**
	 * 用户修改信息
	 * @param user
	 */
	public void updateUserInfo(Users user);

	/**
	 * 查询用户信息
	 * @param userId
	 * @return
	 */
	public Users queryUserInfo(String userId);


	/**
	 * 查询登录用户是否点赞该视频
	 * @param loginUserId 登录用户id
	 * @param videoId 视频id
	 * @return
	 */
	public boolean isUserLikeVideo(String loginUserId, String videoId);

	/**
	 * @Description: 查询用户是否关注
	 */
	public boolean queryIfFollow(String userId, String fanId);


	/**
	 * @Description: 增加用户和粉丝的关注关系
	 */
	public void saveUserFanRelation(String userId, String fanId);

	/**
	 * @Description: 删除用户和粉丝的关系
	 */
	public void deleteUserFanRelation(String userId, String fanId);

	/**
	 * 保存举报信息
	 * @param usersReport
	 */
	void updateReport(UsersReport usersReport);
}
