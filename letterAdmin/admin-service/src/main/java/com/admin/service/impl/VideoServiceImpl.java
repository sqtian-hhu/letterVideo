package com.admin.service.impl;

import com.admin.enums.BGMOperatorTypeEnum;
import com.admin.mapper.BgmMapper;
import com.admin.mapper.UsersReportMapperCustom;
import com.admin.mapper.VideosMapper;
import com.admin.pojo.Bgm;
import com.admin.pojo.BgmExample;
import com.admin.pojo.Videos;
import com.admin.pojo.vo.Reports;
import com.admin.service.VideoService;
import com.admin.utils.JsonUtils;
import com.admin.utils.PagedResult;
import com.admin.utils.ZKCurator;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private BgmMapper bgmMapper;

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private ZKCurator zkCurator;

    @Autowired
    private UsersReportMapperCustom usersReportMapperCustom;

    @Override
    public void addBgm(Bgm bgm) {
        String bgmId = sid.nextShort();
        bgm.setId(bgmId);
        bgmMapper.insert(bgm);

        Map<String,String> map = new HashMap<>();
        map.put("operType",BGMOperatorTypeEnum.ADD.type);

        map.put("path", bgm.getPath());

        //调用zkCurator的方法返回json类型的对象  {"path":"\\bgm\\Zinsin - 随便   Cove.mp3","operType":"1"}
        zkCurator.sendBgmOperator(bgmId, JsonUtils.objectToJson(map));
    }

    @Override
    public void delBgm(String id) {
        Bgm bgm = bgmMapper.selectByPrimaryKey(id);
        bgmMapper.deleteByPrimaryKey(id);

        Map<String,String> map = new HashMap<>();
        map.put("operType",BGMOperatorTypeEnum.DELETE.type);
        map.put("path",bgm.getPath());

        //调用zkCurator的方法返回json类型的对象
        zkCurator.sendBgmOperator(id, JsonUtils.objectToJson(map));
    }

    /**
     * 分页查询BGM列表
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public PagedResult queryBgmList(Integer page,Integer pageSize){
        PageHelper.startPage(page,pageSize);
        BgmExample example = new BgmExample();
        List<Bgm> bgmList = bgmMapper.selectByExample(example);

//        Bgm bgm1 = new Bgm();
//        bgm1.setId("1111");
//        bgm1.setAuthor("测试1");
//        bgm1.setName("测试音乐");
//        bgm1.setPath("sss");
//        List<Bgm> bgmList = new ArrayList<>();
//        bgmList.add(bgm1);

//        System.out.println(bgmList.get(0).toString());
        //List有了,需要再做一层封装
        PageInfo<Bgm> pageList = new PageInfo<>(bgmList);

        PagedResult result = new PagedResult();
        result.setTotal(pageList.getPages());
        result.setRows(bgmList);
        result.setPage(page);
        result.setRecords(pageList.getTotal());

        return result;


    }


    /**
     * 查询举报名单
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult queryReportList(Integer page,Integer pageSize){
        PageHelper.startPage(page,pageSize);
        List<Reports> reportsList = usersReportMapperCustom.selectAllVideoReport();
        PageInfo<Reports> pageList = new PageInfo<>(reportsList);

        PagedResult grid = new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(reportsList);
        grid.setPage(page);
        grid.setRecords(pageList.getTotal());

        return grid;
    }

    @Override
    public void updateVideoStatus(String videoId, Integer status) {


        Videos video = new Videos();
        video.setId(videoId);
        video.setStatus(status);
        videosMapper.updateByPrimaryKeySelective(video);
    }
}
