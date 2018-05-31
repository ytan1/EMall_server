package com.emall.controller.portal;

import com.emall.common.CONST;
import com.emall.common.ResponseCode;
import com.emall.common.ServerResponse;
import com.emall.pojo.Shipping;
import com.emall.pojo.User;
import com.emall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Shipping shipping){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if (user == null) {
            return ServerResponse.responseByError(ResponseCode.LOGIN.getCode(),"Please login first.");
        }
        return iShippingService.add(user.getId(), shipping);
    }

    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse delete(HttpSession session, Integer shippingId){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if (user == null) {
            return ServerResponse.responseByError(ResponseCode.LOGIN.getCode(),"Please login first.");
        }
        return  iShippingService.delete(user.getId(), shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public  ServerResponse update(HttpSession session,Shipping shipping){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if (user == null) {
            return ServerResponse.responseByError(ResponseCode.LOGIN.getCode(),"Please login first.");
        }
        return iShippingService.update(user.getId(), shipping);
    }
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse select(HttpSession session, Integer shippingId){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if (user == null) {
            return ServerResponse.responseByError(ResponseCode.LOGIN.getCode(),"Please login first.");
        }

        return iShippingService.select(user.getId(), shippingId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                    @RequestParam(value="pageSize", defaultValue = "10")Integer pageSize){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if (user == null) {
            return ServerResponse.responseByError(ResponseCode.LOGIN.getCode(),"Please login first.");
        }

        return iShippingService.list(user.getId(), pageNum, pageSize);
    }

}
