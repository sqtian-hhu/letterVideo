package com.letter.dao;


import com.letter.pojo.Videos;
import com.letter.pojo.vo.VideosVO;
import com.letter.utils.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface VideosMapperCustom extends MyMapper<Videos> {

    /**
     * 条件查询所有视频列表
     * @param videoDesc 增加一个通过搜索词查询的参数
     * @return
     */
    public List<VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc);


    /**
     * @Description: 对视频喜欢的数量进行累加
     */
    public void addVideoLikeCount(String videoId);

    /**
     * @Description: 对视频喜欢的数量进行累减
     */
    public void reduceVideoLikeCount(String videoId);

    /**
     * 查询自己的作品
     * @param userId
     * @return
     */
    public List<VideosVO> queryMyWorks(@Param("userId") String userId);

    /**
     * 查询收藏的视频
     * @param userId
     * @return
     */
    public List<VideosVO> queryMyLikeVideos(@Param("userId") String userId);

    /**
     * 查询关注的人的视频
     * @param userId
     * @return
     */
    public List<VideosVO> queryMyFollowVideos(@Param("userId") String userId);



}