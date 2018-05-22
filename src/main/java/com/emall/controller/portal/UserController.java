package com.emall.controller.portal;


import com.emall.common.CONST;
import com.emall.common.ResponseCode;
import com.emall.common.ServerResponse;
import com.emall.dao.UserMapper;
import com.emall.pojo.User;
import com.emall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value="login.do", method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username, password);
        //check if login success, put user data into httpSession
        if(response.isSuccess()){
            session.setAttribute(CONST.CURRENT_USER, response.getData());
        }
        return response;

    }

    @RequestMapping(value="logout.do", method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(CONST.CURRENT_USER);
        return ServerResponse.responseBySuccessMessage("Logout success.");
    }

    @RequestMapping(value="register.do", method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    @RequestMapping(value="check_valid.do", method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type){

        return iUserService.checkValid(str, type);
    }

    //get user info after login : cart no. etc.
    @RequestMapping(value="get_user_info.do", method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user == null){
            return ServerResponse.responseByError(ResponseCode.LOGIN.getCode(), "Need to login first");
        }
        return ServerResponse.responseBySuccess("Get info success.", user);
    }

    //get preset question if user forget password
    @RequestMapping(value="forget_get_question.do", method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.forgetGetQuestion(username);
    }

    @RequestMapping(value="forget_check_answer.do", method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer){
        return iUserService.checkAnswer(username, question, answer);
    }

    @RequestMapping(value="forget_reset_password.do", method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken){
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    @RequestMapping(value="reset_password.do", method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, HttpSession session){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user == null){
            return ServerResponse.responseByError("Please login first.");
        }
        return iUserService.resetPassword(passwordOld, passwordNew, user);

    }

    @RequestMapping(value="update_information.do", method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInfo(User user, HttpSession session){
        User currentUser = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user == null){
            return ServerResponse.responseByError("Please login first.");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response =  iUserService.updateInfo(user);
        if(response.isSuccess()){
            session.setAttribute(CONST.CURRENT_USER, response.getData());
            return response;
        }else{
            return response;
        }
    }

    @RequestMapping(value="get_information.do" , method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user == null){
            return ServerResponse.responseByError("Please login first.");
        }
        return iUserService.getInformation(user.getId());
    }

    @RequestMapping(value = "test.do" )
    @ResponseBody
    public String test(Integer age){
        return "age = " + age;
    }
}
