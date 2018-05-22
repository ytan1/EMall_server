package com.emall.controller.admin;

import com.emall.common.CONST;
import com.emall.common.ServerResponse;
import com.emall.pojo.User;
import com.emall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
@Controller
@RequestMapping(value = "/manage/user/")
public class AdminController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username, password);
        if(response.isSuccess()){
            if(response.getData().getRole() == CONST.ROLE.ADMIN){
                session.setAttribute(CONST.CURRENT_USER, response.getData());
                return response;
            }else{
                return ServerResponse.responseByError("Not an admin account.");
            }
        }
        return response;
    }
}
