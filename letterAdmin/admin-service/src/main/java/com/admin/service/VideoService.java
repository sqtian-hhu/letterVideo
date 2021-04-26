package com.admin.service;

import com.admin.pojo.Bgm;
import com.admin.utils.PagedResult;

public interface VideoService {


    void addBgm(Bgm bgm);

    PagedResult queryBgmList(Integer page, Integer pageSize);

    void delBgm(String id);

    PagedResult queryReportList(Integer page,Integer pageSize);

    void updateVideoStatus(String videoId, Integer status);
}
