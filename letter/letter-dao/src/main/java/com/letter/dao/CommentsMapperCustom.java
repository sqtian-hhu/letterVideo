package com.letter.dao;

import com.letter.pojo.Comments;
import com.letter.pojo.vo.CommentsVO;
import com.letter.utils.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CommentsMapperCustom extends MyMapper<Comments> {

    public List<CommentsVO> queryComments(String videoId);
}