package com.letter.controller;

import com.letter.service.BgmService;
import com.letter.utils.LetterJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "BGM业务接口",tags = {"背景音乐业务的controller"})
@RequestMapping("/bgm")
public class BgmController {

	@Autowired
	private BgmService bgmService;

	@ApiOperation(value = "获取背景音乐列表", notes = "获取背景音乐列表的接口")
	@PostMapping("/list")
	public LetterJSONResult list() {
		return LetterJSONResult.ok(bgmService.queryBgmList());

	}
	
}
