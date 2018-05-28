package com.emall.controller.admin;

import com.emall.common.CONST;
import com.emall.common.ResponseCode;
import com.emall.common.ServerResponse;
import com.emall.dao.OrderItemMapper;
import com.emall.dao.OrderMapper;
import com.emall.pojo.User;
import com.emall.service.IOrderService;
import com.emall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
@Controller
@RequestMapping("/manage/order/")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse manageList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1")Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "5")Integer pageSize){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user == null){
            return ServerResponse.responseByError(ResponseCode.LOGIN.getCode(), ResponseCode.LOGIN.getMsg());
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.responseByError("Please login as admin.");
        }
        return iOrderService.manageList(pageNum, pageSize);
    }
}
