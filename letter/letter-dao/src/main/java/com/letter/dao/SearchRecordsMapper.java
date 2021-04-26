package com.letter.dao;


import com.letter.pojo.SearchRecords;
import com.letter.utils.MyMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SearchRecordsMapper extends MyMapper<SearchRecords> {

    public List<String> getHotwords();
}