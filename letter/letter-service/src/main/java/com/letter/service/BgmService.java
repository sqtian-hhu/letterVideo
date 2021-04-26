package com.letter.service;

import com.letter.pojo.Bgm;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BgmService {


	/**
	 * 查询背景音乐列表
	 * @param
	 * @return
	 */
	public List<Bgm> queryBgmList();

	/**
	 * 根据id查询
	 * @param bgmId
	 * @return
	 */
	public Bgm queryBgmById(String bgmId);

	

}
