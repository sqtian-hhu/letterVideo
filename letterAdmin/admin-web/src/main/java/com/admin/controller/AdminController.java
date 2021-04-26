package com.admin.controller;

import com.admin.pojo.Admin;

import com.admin.service.AdminService;
import com.admin.utils.LetterJSONResult;
import com.admin.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 管理员用户
 */
@Controller
@RequestMapping()
public class AdminController {

    @GetMapping("hello")
    public String hello(){
        return "hello";
    }

    @GetMapping("center")
    public String center(){
        return "center";
    }

//    @GetMapping("login")
//    public String login() {
//        return "login";
//    }




    @Autowired
    private AdminService adminService;

    @RequestMapping("findAll")
    public String findAll(Model model){

        List<Admin> adminList = adminService.findAll();
        //存入model
        model.addAttribute("list",adminList);

        return adminList.toString();
    }




    //    @RequestMapping("/register")
//    public String register(String username, String password, HttpServletRequest request, HttpServletResponse response) throws Exception {
//
//        //1. 判断用户名和密码必须不为空
//        if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
//            return "don't be blank";
//        }
//
//        //2. 判断用户名存在
//        boolean isExist = adminService.queryUsernameIsExist(username);
//
//        if(isExist){
//           return "have the same username";
//        }else {
//
//
//            Admin admin = new Admin();
//            admin.setUsername(username);
//            admin.setPassword(MD5Utils.getMD5Str(password));
//            adminService.saveAdmin(admin);
////            response.sendRedirect(request.getContextPath() + "/admin/findAll");
//            return "login";
//        }
//    }
//
//
//    @RequestMapping("/login")
//    public String login(String username, String password, HttpServletRequest request, HttpServletResponse response) throws Exception {
//
//
//        //1. 判断用户名和密码必须不为空
//        if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
//            return "username or password is null";
//        }
//        //2. 判断用户是否存在
//        Admin adminResult = adminService.queryForLogin(username,MD5Utils.getMD5Str(password));
//
//        //3. 返回
//        if(adminResult != null){
//            adminResult.setPassword("");
//
////            return adminResult.toString();
//            return "center";
//        }else {
//            return "username or password is not true";
//        }
//    }


//	@GetMapping("/login")
//	public String login() {
//		return "login";
//	}
//
	@PostMapping("/login")
	@ResponseBody
	public LetterJSONResult userLogin(String username, String password,
                                      HttpServletRequest request, HttpServletResponse response) throws Exception {

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

//	@GetMapping("/logout")
//	public String logout(HttpServletRequest request, HttpServletResponse response) {
//		request.getSession().removeAttribute("sessionUser");
//		return "login";
//	}


//    @GetMapping("/login")
//    public String login() {
//        return "login";
//    }
//
//    @PostMapping("login")
//    @ResponseBody
//    public IMoocJSONResult userLogin(String username, String password,
//                                     HttpServletRequest request, HttpServletResponse response) {
//
//        // TODO 模拟登陆
//        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
//            return IMoocJSONResult.errorMap("用户名和密码不能为空");
//        } else if (username.equals("tsq") && password.equals("tsq")) {
//
//            String token = UUID.randomUUID().toString();
//            Admin user = new Admin(token,username, password);
//            request.getSession().setAttribute("sessionUser", user);
//            return IMoocJSONResult.ok();
//        }
//
//        return IMoocJSONResult.errorMsg("登陆失败，请重试...");
//    }
//
//    @GetMapping("/logout")
//    public String logout(HttpServletRequest request, HttpServletResponse response) {
//        request.getSession().removeAttribute("sessionUser");
//        return "login";
//    }

}
