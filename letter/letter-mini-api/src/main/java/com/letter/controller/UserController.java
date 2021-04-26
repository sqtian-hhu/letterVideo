package com.letter.controller;

import com.letter.pojo.Users;
import com.letter.pojo.UsersReport;
import com.letter.pojo.vo.PublisherVideo;
import com.letter.pojo.vo.UsersVO;
import com.letter.service.UserService;
import com.letter.utils.LetterJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


@RestController
//swagger2自动生成api, localhost:8080/swagger-ui.html
@Api(value = "用户相关业务接口",tags = {"用户相关业务的controller"})
@RequestMapping("/user")
public class UserController extends BasicController{

	@Autowired
	private UserService userService;

	/**
	 * 用户上传头像的方法
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "用户上传头像",notes = "用户上传头像的接口 ")
	@ApiImplicitParam(name = "userId", value = "用户id",required = true, dataType = "String", paramType = "query")

	@PostMapping("/uploadFace")
	//添加RequestParam注解,不使用该注解要求controller方法中的参数名称要跟form中name名称一致，使用该注解方便随意取参数名称，不过value属性还是要与name一致
	//MultipartFile 其实例对象代表了一个在multipart请求中接收到的待上传文件
	public LetterJSONResult uploadFace(String userId, @RequestParam("file") MultipartFile[] files) throws Exception {

		if(StringUtils.isBlank(userId)){
			return LetterJSONResult.errorMsg("用户id不能为空...");
		}



		//统一文件保存的命名空间
		String fileSpace = "E:/letter_videos_dev";
		//保存到数据库中的相对路径
		String uploadPathDB = "/" + userId + "/face";

		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;

		try{
			if (files != null && files.length > 0){


				String originalFilename = files[0].getOriginalFilename();
				if(StringUtils.isNotBlank(originalFilename)){
					//文件上传的最终保存路径
					String finalFacePath = fileSpace + uploadPathDB + "/" + originalFilename;
					//设置数据库保存的路径
					uploadPathDB += ("/" + originalFilename);

					File outFile = new File(finalFacePath);
					if(outFile.getParentFile() != null || !outFile.getParentFile().isDirectory() ){
						//如果保存头像的父文件夹不为空或者不是文件夹, 创建父文件夹
						outFile.getParentFile().mkdirs();
					}

					//输出到最终文件目录的流
					fileOutputStream = new FileOutputStream(outFile);
					//上传文件的输入流
					inputStream = files[0].getInputStream();
					//上传文件拷贝到服务器最终文件夹里
					IOUtils.copy(inputStream,fileOutputStream);
				}

			}else{
				return LetterJSONResult.errorMsg("上传出错");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return LetterJSONResult.errorMsg("上传出错");
		}finally{
			if(fileOutputStream != null){
				fileOutputStream.flush();
				fileOutputStream.close();
			 }
		}

		Users user = new Users();
		user.setId(userId);
		user.setFaceImage(uploadPathDB);
		userService.updateUserInfo(user);

		//返回存储路径
		return LetterJSONResult.ok(uploadPathDB);
	}


	/**
	 * 查询信息
	 * @param userId
	 * @return
	 */
	@ApiOperation(value = "查询用户信息",notes = "查询用户信息的接口 ")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "用户id",required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "fanId", value = "粉丝id",required = true, dataType = "String", paramType = "query")})
	@PostMapping("/query")
	public LetterJSONResult query(String userId, String fanId) {

		if (StringUtils.isBlank(userId)){
			return LetterJSONResult.errorMsg("用户id不能为空");
		}

		Users userInfo = userService.queryUserInfo(userId);
		UsersVO userVO = new UsersVO();
		BeanUtils.copyProperties(userInfo,userVO);
		userVO.setFollow(userService.queryIfFollow(userId,fanId));

		return LetterJSONResult.ok(userVO);
	}


//	@ApiOperation(value = "查询用户信息",notes = "查询用户信息的接口 ")
//	@PostMapping("/queryTest")
//	public LetterJSONResult queryTest(String userId){
//		System.out.println("userId:"+userId);
//		Users userInfo = userService.queryUserInfo(userId);
//		UsersVO userVO = new UsersVO();
//		BeanUtils.copyProperties(userInfo,userVO);
//		return LetterJSONResult.ok(userVO);
//	}

	@ApiOperation(value = "查询发布者信息",notes = "查询发布者信息的接口 ")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "loginUserId", value = "登录用户id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "videoId", value = "视频id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "publishUserId", value = "发布用户id", required = true, dataType = "String", paramType = "query")
	})
	@PostMapping("/queryPublisher")
	public LetterJSONResult queryPublisher(String loginUserId,String videoId,String publishUserId){
		if (StringUtils.isBlank(publishUserId)) {
			return LetterJSONResult.errorMsg("");
		}

		//1. 查询视频发布者的信息
		Users publisherInfo = userService.queryUserInfo(publishUserId);
		UsersVO publisher = new UsersVO();
		BeanUtils.copyProperties(publisherInfo,publisher);

		//2. 查询当前登陆者和视频的点赞关系
		boolean userLikeVideo = userService.isUserLikeVideo(loginUserId,videoId);

		PublisherVideo publisherVideo = new PublisherVideo();
		publisherVideo.setPublisher(publisher);
		publisherVideo.setUserLikeVideo(userLikeVideo);
		return LetterJSONResult.ok(publisherVideo);

	}


	@ApiOperation(value = "关注发布者",notes = "关注发布者的接口 ")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "作者id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "fanId", value = "粉丝id", required = true, dataType = "String", paramType = "query"),
	})
	@PostMapping("/beyourfans")
	public  LetterJSONResult beyourfans(String userId,String fanId){
		//在users_fans表中添加对应关系

		//1. 判空
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
			return LetterJSONResult.errorMsg("ID不能为空");
		}

		userService.saveUserFanRelation(userId, fanId);

		return LetterJSONResult.ok("关注成功...");
	}

	@ApiOperation(value = "取消关注",notes = "取消关注的接口 ")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "作者id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "fanId", value = "粉丝id", required = true, dataType = "String", paramType = "query"),
	})
	@PostMapping("/dontbeyourfans")
	public  LetterJSONResult dontbeyourfans(String userId,String fanId){
		//在users_fans表中添加对应关系

		//1. 判空
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
			return LetterJSONResult.errorMsg("ID不能为空");
		}

		userService.deleteUserFanRelation(userId, fanId);

		return LetterJSONResult.ok("取消关注...");
	}


	@PostMapping("/reportUser")
	public LetterJSONResult report(@RequestBody UsersReport usersReport){
		userService.updateReport(usersReport);
		return LetterJSONResult.ok("举报成功!");
	}
}



