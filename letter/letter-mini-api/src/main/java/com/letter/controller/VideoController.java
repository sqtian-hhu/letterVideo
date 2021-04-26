package com.letter.controller;

import com.letter.enums.VideoStatusEnum;
import com.letter.pojo.Bgm;
import com.letter.pojo.Comments;
import com.letter.pojo.Videos;
import com.letter.service.BgmService;
import com.letter.service.VideoService;
import com.letter.utils.FetchVideoCover;
import com.letter.utils.LetterJSONResult;
import com.letter.utils.MergeVideoMp3;
import com.letter.utils.PagedResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

//@RestController是一个组合注解，写在类上面，是组合了@ResponseBody和@Controller，默认了类中所有的方法都包含ResponseBody注解的一种简写形式
@RestController
@Api(value = "视频业务接口",tags = {"视频业务的controller"})
@RequestMapping("/video")
public class VideoController extends BasicController{

	@Autowired
	private VideoService videoService;

	@Autowired
	private BgmService bgmService;


	private String finalVideoPath;
	private String finalCoverPath;


	/**
	 * 用户上传视频的接口
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "用户上传视频",notes = "用户上传视频的接口 ")

	@ApiImplicitParams({
			@ApiImplicitParam(name= "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name= "bgmId", value = "背景音乐id", required = false, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name= "videoSeconds", value = "视频播放长度", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name= "videoWidth", value = "视频宽度", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name= "videoHeight", value = "视频高度", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name= "desc", value = "视频描述", required = false, dataType = "String", paramType = "form")
	})

	// wx.uploadFile(Object object)
	//将本地资源上传到服务器。客户端发起一个 HTTPS POST 请求，其中 content-type 为 multipart/form-data。
	@PostMapping( value="/upload",headers = "content-type=multipart/form-data")
	//添加RequestParam注解,不使用该注解要求controller方法中的参数名称要跟form中name名称一致，使用该注解方便随意取参数名称，不过value属性还是要与name一致
	//MultipartFile 其实例对象代表了一个在multipart请求中接收到的待上传文件
	public LetterJSONResult upload(String userId,
								   String bgmId, double videoSeconds, int videoWidth, int videoHeight,
								   String desc,
								   @ApiParam(value = "短视频", required = true)
											   MultipartFile file) throws Exception {

		if(StringUtils.isBlank(userId)){
			return LetterJSONResult.errorMsg("用户id不能为空...");
		}



		//统一文件保存的命名空间
		//写到basicController中复用
//		String fileSpace = "E:/letter_videos_dev";
		//保存到数据库中的相对路径
		String uploadPathDB = "/" + userId + "/video";
		String coverPathDB = "/" + userId + "/video";

		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;

		try{
			String originalFilename = file.getOriginalFilename();
			//abc.mp4

			//abc

			
			String[] strings = originalFilename.split("\\.");

			String fileNamePrefix = strings[0];
			if (strings.length>2){
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < strings.length-1; i++) {
					sb.append(strings[i]);
				}
				fileNamePrefix = sb.toString();
			}


			//System.out.println(originalFilename);

			if(StringUtils.isNotBlank(originalFilename)){
				//文件上传的最终保存路径
				finalVideoPath = FILE_SPACE + uploadPathDB + "/" + originalFilename;
				//设置数据库保存的路径
				uploadPathDB += ("/" + originalFilename);
				coverPathDB += ("/" + fileNamePrefix + ".jpg");

				File outFile = new File(finalVideoPath);
				if(outFile.getParentFile() != null || !outFile.getParentFile().isDirectory() ){
					//如果保存头像的父文件夹不为空或者不是文件夹, 创建父文件夹
					outFile.getParentFile().mkdirs();
				}

				//输出到最终文件目录的流 outFIle
//				System.out.println(outFile);
				fileOutputStream = new FileOutputStream(outFile);
				//上传文件的输入流
				inputStream = file.getInputStream();
				//上传文件拷贝到服务器最终文件夹里
				IOUtils.copy(inputStream,fileOutputStream);
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

		//判断bgmId是否为空,如果不为空
		//就查询bgm信息,合并视频
		if(StringUtils.isNotBlank(bgmId)){
			Bgm bgm = bgmService.queryBgmById(bgmId);
			String mp3InputPath = FILE_SPACE + bgm.getPath();

			MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
			String videoInputPath = finalVideoPath;

			String videoOutputName = UUID.randomUUID().toString()+".mp4";
			uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
			finalVideoPath = FILE_SPACE  + uploadPathDB;
			tool.convertor(videoInputPath,mp3InputPath,videoSeconds,finalVideoPath);

		}

//		System.out.println("uploadPathDB=" + uploadPathDB);
//		System.out.println("finalVideoPath=" + finalVideoPath);

		//对视频截图获取封面
		FetchVideoCover videoInfo = new FetchVideoCover(FFMPEG_EXE);
		videoInfo.fetchCover(finalVideoPath,FILE_SPACE + coverPathDB);




		//保存视频信息到数据库
		Videos video = new Videos();
		video.setAudioId(bgmId);
		video.setUserId(userId);
		video.setVideoSeconds((float)videoSeconds);
		video.setVideoHeight(videoHeight);
		video.setVideoWidth(videoWidth);
		video.setVideoDesc(desc);
		video.setVideoPath(uploadPathDB);
		video.setStatus(VideoStatusEnum.SUCCESS.value);
		video.setCreateTime(new Date());

		video.setCoverPath(coverPathDB);

		String videoId = videoService.saveVideo(video);

		//返回存储路径
		return LetterJSONResult.ok(videoId);
	}



	/**
	 * 用户上传封面的接口
	 * @param videoId
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "用户上传封面",notes = "用户上传封面的接口 ")

	@ApiImplicitParams({
			@ApiImplicitParam(name= "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name= "videoId", value = "视频主键id", required = true, dataType = "String", paramType = "form"),
				})
	@PostMapping( value="/uploadCover",headers = "content-type=multipart/form-data")
	public LetterJSONResult uploadCover(String userId,String videoId,
								   @ApiParam(value = "视频封面", required = true)
										   MultipartFile file) throws Exception {

		if(StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)){
			return LetterJSONResult.errorMsg("视频主键和用户id不能为空...");
		}



		//保存到数据库中的相对路径
		String uploadPathDB = "/" + userId + "/video";

		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;

		try{
			String originalFilename = file.getOriginalFilename();
			if(StringUtils.isNotBlank(originalFilename)){
				//文件上传的最终保存路径
				finalCoverPath = FILE_SPACE+ uploadPathDB + "/" + originalFilename;
				//设置数据库保存的路径
				uploadPathDB += ("/" + originalFilename);

				File outFile = new File(finalCoverPath );
				if(outFile.getParentFile() != null || !outFile.getParentFile().isDirectory() ){
					//如果保存头像的父文件夹不为空或者不是文件夹, 创建父文件夹
					outFile.getParentFile().mkdirs();
				}

				//输出到最终文件目录的流 outFIle
//				System.out.println(outFile);
				fileOutputStream = new FileOutputStream(outFile);
				//上传文件的输入流
				inputStream = file.getInputStream();
				//上传文件拷贝到服务器最终文件夹里
				IOUtils.copy(inputStream,fileOutputStream);
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


		videoService.updateVideo(videoId,uploadPathDB);
		//返回存储路径
		return LetterJSONResult.ok();
	}

	/**
	 * 分页展示所有视频
	 * isSaveRecord: 1-需要保存
	 * 				 0-或空时,不需要保存
	 * @param page 分页页数
	 * @return
	 */

	@ApiOperation(value = "分页展示所有视频",notes = "分页展示所有视频的接口 ")
	@ApiImplicitParams({
			@ApiImplicitParam(name= "video", value = "视频", required = true, dataType = "Videos", paramType = "form"),
			@ApiImplicitParam(name= "isSaveRecord", value = "保存热搜词", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name= "page", value = "分页显示页数", required = true, dataType = "Integer", paramType = "form"),

	})
	@PostMapping("/showAll")
	public LetterJSONResult showAll(@RequestBody Videos video, Integer isSaveRecord, Integer page) {

		if(page == null){
			page = 1;
		}

		PagedResult result = videoService.getAllVideos(video,isSaveRecord,page,PAGE_SIZE);
		return LetterJSONResult.ok(result);
	}


	@ApiOperation(value = "获取热搜词",notes = "获取热搜词的接口 ")
	@PostMapping("/hot")
	public LetterJSONResult hot() {
		return LetterJSONResult.ok(videoService.getHotwords());
	}


	@ApiOperation(value = "保存评论",notes = "保存评论的接口 ")
	@ApiImplicitParam(name= "comment", value = "评论内容", required = true, dataType = "Comments", paramType = "form")
	@PostMapping("/saveComment")
	public LetterJSONResult saveComment(@RequestBody Comments comment){
		videoService.saveComment(comment);
		return LetterJSONResult.ok();
	}

	@ApiOperation(value = "获取评论",notes = "获取评论的接口 ")
	@ApiImplicitParams({
			@ApiImplicitParam(name= "videoId", value = "视频Id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name= "page", value = "分页显示页数", required = true, dataType = "Integer", paramType = "form"),
			@ApiImplicitParam(name= "pageSize", value = "每页显示大小", required = true, dataType = "Integer", paramType = "form"),

	})
	@PostMapping("/getVideoComments")
	public LetterJSONResult getVideoComments(String videoId,Integer page,Integer pageSize){
		if(StringUtils.isBlank(videoId)){
			return LetterJSONResult.ok();
		}

		//分页查询评论列表,时间顺序倒序排序
		if(page == null){
			page = 1;
		}

		if (pageSize == null){
			pageSize = 10;
		}

		PagedResult list = videoService.getAllComments(videoId,page,pageSize);

		return LetterJSONResult.ok(list);
	}


	@ApiOperation(value = "点击收藏",notes = "点击收藏的接口 ")
	@ApiImplicitParams({
			@ApiImplicitParam(name= "userId", value = "用户Id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name= "videoId", value = "视频Id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name= "videoCreaterId", value = "视频作者Id", required = true, dataType = "String", paramType = "form"),
	})
	@PostMapping("/userLike")
	public LetterJSONResult userLike(String userId,String videoId,String videoCreaterId){

		videoService.userLikeVideo(userId, videoId, videoCreaterId);
		return LetterJSONResult.ok();
	}

	@ApiOperation(value = "取消收藏",notes = "取消收藏的接口 ")
	@ApiImplicitParams({
			@ApiImplicitParam(name= "userId", value = "用户Id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name= "videoId", value = "视频Id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name= "videoCreaterId", value = "视频作者Id", required = true, dataType = "String", paramType = "form"),
	})
	@PostMapping("/userUnLike")
	public LetterJSONResult userUnLike(String userId,String videoId,String videoCreaterId){
		videoService.userUnLikeVideo(userId, videoId, videoCreaterId);
		return LetterJSONResult.ok();
	}

	@ApiOperation(value = "展示我的作品",notes = "展示我的作品的接口 ")
	@ApiImplicitParams({
			@ApiImplicitParam(name= "userId", value = "用户Id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name= "page", value = "分页显示页数", required = true, dataType = "Integer", paramType = "form"),
			@ApiImplicitParam(name= "pageSize", value = "每页显示大小", required = true, dataType = "Integer", paramType = "form"),
	})
	@PostMapping("/showMyWork")
	public LetterJSONResult showMyWork(String userId,Integer page, Integer pageSize){
		if (StringUtils.isBlank(userId)){
			return LetterJSONResult.errorMsg("userId不能为空");
		}

//		分页

		if (page == null) {
			page = 1;
		}

		if (pageSize == null) {
			pageSize = 6;
		}

		//调用业务层查找列表
		PagedResult videosList = videoService.queryMyWorks(userId, page, pageSize);

		return LetterJSONResult.ok(videosList);

	}

	/**
	 * 展示我的收藏
	 * @param userId
	 * @param page
	 * @param pageSize
	 * @return
	 */

	@ApiOperation(value = "展示我的收藏",notes = "展示我的收藏的接口 ")
	@ApiImplicitParams({
			@ApiImplicitParam(name= "userId", value = "用户Id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name= "page", value = "分页显示页数", required = true, dataType = "Integer", paramType = "form"),
			@ApiImplicitParam(name= "pageSize", value = "每页显示大小", required = true, dataType = "Integer", paramType = "form"),
	})
	@PostMapping("/showMyLike")
	public LetterJSONResult showMyLike(String userId,Integer page, Integer pageSize) {
		if (StringUtils.isBlank(userId)){
			return LetterJSONResult.errorMsg("userId不能为空");
		}

//		分页

		if (page == null) {
			page = 1;
		}

		if (pageSize == null) {
			pageSize = 6;
		}

		//调用业务层查找列表
		PagedResult videosList = videoService.queryMyLikeVideos(userId, page, pageSize);

		return LetterJSONResult.ok(videosList);

	}


	/**
	 * 显示关注人的视频
	 * @param userId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@ApiOperation(value = "展示关注人作品",notes = "展示关注人作品的接口 ")
	@ApiImplicitParams({
			@ApiImplicitParam(name= "userId", value = "用户Id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name= "page", value = "分页显示页数", required = true, dataType = "Integer", paramType = "form"),
			@ApiImplicitParam(name= "pageSize", value = "每页显示大小", required = true, dataType = "Integer", paramType = "form"),
	})
	@PostMapping("/showMyFollow")
	public LetterJSONResult showMyFollow(String userId,Integer page,Integer pageSize){
		if (StringUtils.isBlank(userId)) {
			return LetterJSONResult.errorMsg("userId不能为空");
		}

		if (page == null){
			page = 1;
		}

		if (pageSize == null) {
			pageSize = 6;
		}


		PagedResult videosList = videoService.queryMyFollowVideos(userId,page,pageSize);


		return LetterJSONResult.ok(videosList);

	}

}
