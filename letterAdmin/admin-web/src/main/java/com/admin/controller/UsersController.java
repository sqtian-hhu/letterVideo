package com.admin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.admin.service.AdminService;
import com.admin.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.admin.pojo.Admin;
import com.admin.pojo.Users;
import com.admin.service.UsersService;
import com.admin.utils.LetterJSONResult;
import com.admin.utils.PagedResult;


/**
 * 小程序用户
 */
@Controller
@RequestMapping("/users")
public class UsersController {

	@Autowired
	private UsersService usersService;
	@Autowired
	private AdminService adminService;



	@GetMapping("/showList")
	public String showList() {
		return "users/usersList";
	}
	
	@PostMapping("/list")
	@ResponseBody
	public PagedResult list(Users user , Integer page) {

		PagedResult result = usersService.queryUsers(user, page == null ? 1 : page, 10);
		return result;
	}


	/**
	 * 为什么我想把login放到AdminController里面,我特么都把/users/login全改成admin/gin了为什么还是有提示users/login
	 * @return
	 */
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@PostMapping("/login")
	@ResponseBody
	public LetterJSONResult login(String username, String password,
									  HttpServletRequest request, HttpServletResponse response) throws Exception {

//		// TODO 模拟登陆
//		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
//			return IMoocJSONResult.errorMap("用户名和密码不能为空");
//		} else if (username.equals("tsq") && password.equals("tsq")) {
//
//			String token = UUID.randomUUID().toString();
//			Admin user = new Admin(token,username, password);
//			request.getSession().setAttribute("sessionUser", user);
//			return IMoocJSONResult.ok();
//		}
//
//		return IMoocJSONResult.errorMsg("登陆失败，请重试...");

		//1. 判空
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			return LetterJSONResult.errorMap("用户名和密码不能为空");
		}

        //2. 判断用户是否存在

		Admin adminResult = adminService.queryForLogin(username, MD5Utils.getMD5Str(password));

		if(adminResult == null){
            return LetterJSONResult.errorMsg("用户不存在，请先注册...");
        }

//        String token = UUID.randomUUID().toString();
//		Admin user = new Admin(token,username, password);

		request.getSession().setAttribute("sessionUser", adminResult);
		return LetterJSONResult.ok();

	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		request.getSession().removeAttribute("sessionUser");
		return "login";
	}



}
