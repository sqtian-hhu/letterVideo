package com.admin.mapper;

import com.admin.pojo.vo.Reports;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface UsersReportMapperCustom {
    List<Reports> selectAllVideoReport();
}