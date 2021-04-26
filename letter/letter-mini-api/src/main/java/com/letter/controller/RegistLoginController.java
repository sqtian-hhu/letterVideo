package com.letter.controller;

import com.letter.pojo.Users;
import com.letter.pojo.vo.UsersVO;
import com.letter.service.UserService;
import com.letter.utils.LetterJSONResult;
import com.letter.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
//swagger2自动生成api, localhost:8080/swagger-ui.html
@Api(value = "用户注册接口",tags = {"注册和登录的controller"})
public class RegistLoginController extends BasicController{

	@Autowired
	private UserService userService;

	/**
	 * 用户注册的方法
	 * @param user
	 * @return
	 * @throws Exception
	 */

	@ApiOperation(value = "用户注册",notes = "用户注册的接口 ")
	//同时Users也要添加ApiModel注解
	@PostMapping("/regist")
	//RequestBody 接收json对象
	public LetterJSONResult regist(@RequestBody Users user) throws Exception {

		//1. 判断用户名和密码必须不为空

		if(StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())){
			return LetterJSONResult.errorMsg("用户名和密码不能为空");
		}
		//2. 判断用户名存在

		boolean isExist = userService.queryUsernameIsExist(user.getUsername());

		//3. 保存用户注册信息
		if(!isExist){
			user.setNickname(user.getUsername());
			user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
			user.setFansCounts(0);
			user.setReceiveLikeCounts(0);
			user.setFollowCounts(0);

			userService.saveUser(user);
		} else {
			return LetterJSONResult.errorMsg("用户名已存在,请换一个再试");
		}

		//考虑到安全问题,user保存到数据库后,再把password设置为空
		user.setPassword("");

		//使用redis

		UsersVO userVO = setUserRedisSessionToken(user);

		return LetterJSONResult.ok(userVO);
	}

	/**
	 * 用户登录的方法
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "用户登录",notes = "用户登录的接口 ")
	@PostMapping("/login")
	//要添加RequestBody才能获取请求体
	public LetterJSONResult login(@RequestBody Users user) throws Exception {

		System.out.println("连接服务器登录");

		String username = user.getUsername();
		String password = user.getPassword();
//		System.out.println(username);
//		System.out.println(password);

		//1. 判断用户名和密码必须不为空
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)){
			return LetterJSONResult.errorMsg("用户名或密码不能为空...");
		}

		//2. 判断用户是否存在
		Users userResult = userService.queryUserForLogin(username,MD5Utils.getMD5Str(user.getPassword()));

		//3. 返回
		if(userResult != null){
			userResult.setPassword("");

			UsersVO userVO = setUserRedisSessionToken(userResult);
			return LetterJSONResult.ok(userVO);
		}else {
			return LetterJSONResult.errorMsg("用户名或密码不正确,请重试...");
		}
	}

	/**
	 * 用户注销的方法
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "用户注销",notes = "用户注销的接口 ")
	@ApiImplicitParam(name = "userId", value = "用户id",required = true, dataType = "String", paramType = "query")
	@PostMapping("/logout")
	//要添加RequestBody才能获取请求体
	public LetterJSONResult logout(String userId) throws Exception {
		//注销其实就是将之前redis中保存的session关系删除
		redis.del(USER_REDIS_SESSION + ":" + userId);
		return LetterJSONResult.ok("用户已注销");
	}



	public UsersVO setUserRedisSessionToken(Users userModel) {
		String uniqueToken = UUID.randomUUID().toString();
		//使用冒号方便在redis Manager里分类查看
		redis.set(USER_REDIS_SESSION + ":" + userModel.getId(),uniqueToken,1000*60*30);

		UsersVO userVO = new UsersVO();
		BeanUtils.copyProperties(userModel,userVO);
		userVO.setUserToken(uniqueToken);
		return userVO;
	}
	
}
