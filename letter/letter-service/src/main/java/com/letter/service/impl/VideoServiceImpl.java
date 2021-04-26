package com.letter.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.letter.dao.*;
import com.letter.pojo.Comments;
import com.letter.pojo.SearchRecords;
import com.letter.pojo.UsersLikeVideos;
import com.letter.pojo.Videos;
import com.letter.pojo.vo.CommentsVO;
import com.letter.pojo.vo.VideosVO;
import com.letter.service.VideoService;
import com.letter.utils.PagedResult;
import com.letter.utils.TimeAgoUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;


@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private VideosMapperCustom videosMapperCustom;

    @Autowired
    private Sid sid;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private CommentsMapperCustom commentsMapperCustom;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;


    @Transactional(propagation = Propagation.REQUIRED )
    public String saveVideo(Videos video) {

        String id = sid.nextShort();
        video.setId(id);
        //insertSelective不会保存null,而是使用数据库默认值,
        //insert会直接保存null值
        videosMapper.insertSelective(video);

        //返回视频的id
        return id;
    }

    @Transactional(propagation = Propagation.REQUIRED )
    public String updateVideo(String videoId, String coverPath) {

        Videos video = new Videos();
        video.setId(videoId);
        video.setCoverPath(coverPath);

        //Selective不会把null覆盖到已经有值的属性
        videosMapper.updateByPrimaryKeySelective(video);
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize) {

        //保存热搜词
        String desc = video.getVideoDesc();
        if(isSaveRecord != null && isSaveRecord == 1){
            SearchRecords record = new SearchRecords();
            String recordId = sid.nextShort();
            record.setId(recordId);
            record.setContent(desc);

            searchRecordsMapper.insert(record);

        }



        //分页显示
        PageHelper.startPage(page,pageSize);

        List<VideosVO> list = videosMapperCustom.queryAllVideos(desc);

        PageInfo<VideosVO> pageList = new PageInfo<VideosVO>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());
        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<String> getHotwords() {

        return searchRecordsMapper.getHotwords();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveComment(Comments comment) {
        String id = sid.nextShort();
        comment.setId(id);
        comment.setCreateTime(new Date());
        commentsMapper.insert(comment);
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);

        List<CommentsVO> list = commentsMapperCustom.queryComments(videoId);
        for (CommentsVO c : list) {
            String timeAgo = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(timeAgo);
        }

        //分页显示评论
        PageInfo<CommentsVO> pageList = new PageInfo<CommentsVO>(list);

        PagedResult grid = new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(list);
        grid.setPage(page);
        grid.setRecords(pageList.getTotal());


        return grid;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void userLikeVideo(String userId, String videoId, String videoCreaterId) {

        //1. 保存用户和视频的喜欢点赞关联关系表
        String likeId = sid.nextShort();
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();
        usersLikeVideos.setId(likeId);
        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoId);

        usersLikeVideosMapper.insert(usersLikeVideos);

        //2. 视频喜欢数量累加
        videosMapperCustom.addVideoLikeCount(videoId);

        //3. 用户受喜欢数量累加
        usersMapper.addReceiveLikeCount(videoCreaterId);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void userUnLikeVideo(String userId, String videoId, String videoCreaterId) {

        //1. 删除用户和视频的喜欢点赞关系表
        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);

        usersLikeVideosMapper.deleteByExample(example);

        //2. 视频喜欢数量累减
        videosMapperCustom.reduceVideoLikeCount(videoId);

        //3. 用户受喜欢数量累减
        usersMapper.reduceReceiveLikeCount(videoCreaterId);


    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedResult queryMyWorks(String userId, Integer page, Integer pageSize) {
        //分页显示
        PageHelper.startPage(page,pageSize);

        List<VideosVO> list = videosMapperCustom.queryMyWorks(userId);

        PageInfo<VideosVO> pageList = new PageInfo<VideosVO>(list);
        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());
        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {

        //分页显示
        PageHelper.startPage(page,pageSize);
        List<VideosVO> list = videosMapperCustom.queryMyLikeVideos(userId);

        PageInfo<VideosVO> pageList = new PageInfo<VideosVO>(list);
        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());
        return pagedResult;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize) {
        //分页显示
        PageHelper.startPage(page,pageSize);

        List<VideosVO> list = videosMapperCustom.queryMyFollowVideos(userId);

        PageInfo<VideosVO> pageList = new PageInfo<VideosVO>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }


}
