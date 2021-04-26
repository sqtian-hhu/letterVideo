package com.letter.service;

import com.letter.pojo.Comments;
import com.letter.pojo.Videos;
import com.letter.utils.PagedResult;

import java.util.List;

public interface VideoService {

	/**
	 * 保存视频
	 * @param video
	 */

	public String saveVideo(Videos video);


	/**
	 * 更新视频封面
	 * @param videoId
	 * @param coverPath
	 * @return
	 */
	public String updateVideo(String videoId,String coverPath);


	/**
	 * 分页查询视频列表
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult getAllVideos(Videos video, Integer isSaveRecord,Integer page, Integer pageSize);


	/**
	 * 获取热搜词列表
	 * @return
	 */
	public List<String> getHotwords();


	/**
	 * 用户留言
	 * @param comment
	 */
	public void saveComment(Comments comment);

	/**
	 * 留言分页显示
	 * @param videoId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult getAllComments(String videoId, Integer page, Integer pageSize);

	/**
	 * 收藏
	 * @param userId
	 * @param videoId
	 * @param videoCreaterId
	 */
	public void userLikeVideo(String userId, String videoId, String videoCreaterId);

	/**
	 * 取消收藏
	 * @param userId
	 * @param videoId
	 * @param videoCreaterId
	 */
	public void userUnLikeVideo(String userId, String videoId, String videoCreaterId);


	/**
	 * 查询自己的作品
	 * @param userId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult queryMyWorks(String userId,Integer page,Integer pageSize);

	/**
	 * 查询收藏的视频
	 * @param userId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult queryMyLikeVideos(String userId,Integer page,Integer pageSize);


	/**
	 * 查询关注人的视频
	 * @param userId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult queryMyFollowVideos(String userId,Integer page,Integer pageSize);

}
