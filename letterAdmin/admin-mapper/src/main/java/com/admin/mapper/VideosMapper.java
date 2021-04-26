package com.admin.mapper;


import com.admin.pojo.Videos;
import java.util.List;

import com.admin.pojo.VideosExample;
import com.admin.utils.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VideosMapper {
    int countByExample(VideosExample example);

    int deleteByExample(VideosExample example);

    int deleteByPrimaryKey(String id);

    int insert(Videos record);

    int insertSelective(Videos record);

    List<Videos> selectByExample(VideosExample example);

    Videos selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") Videos record, @Param("example") VideosExample example);

    int updateByExample(@Param("record") Videos record, @Param("example") VideosExample example);

    /**
     * 改有值的属性
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(Videos record);

    /**
     * 更新全部属性
     * @param record
     * @return
     */
    int updateByPrimaryKey(Videos record);
}