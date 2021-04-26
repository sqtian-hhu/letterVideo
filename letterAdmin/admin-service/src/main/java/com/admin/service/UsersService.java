package com.admin.service;

import com.admin.pojo.Users;
import com.admin.utils.PagedResult;

public interface UsersService {

	public PagedResult queryUsers(Users user, Integer page, Integer pageSize);
	
}
