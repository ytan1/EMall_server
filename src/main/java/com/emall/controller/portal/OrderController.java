package com.emall.controller.portal;

import com.emall.common.CONST;
import com.emall.common.ServerResponse;
import com.emall.pojo.User;
import com.emall.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/order/")
public class OrderController {


    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse createOrder(HttpSession session, Integer shippingId){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user==null){
            return ServerResponse.responseByError("Please login first.");
        }
        return iOrderService.createOrder(user.getId(), shippingId);

    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancleOrder(HttpSession session, long orderNo){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user==null){
            return ServerResponse.responseByError("Please login first.");
        }
        return iOrderService.cancelOrder(user.getId(), orderNo);

    }

    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession session){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user==null){
            return ServerResponse.responseByError("Please login first.");
        }
        return iOrderService.getOrderCartProduct(user.getId());

    }
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1")Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "5")Integer pageSize){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user==null){
            return ServerResponse.responseByError(10, "Please login first.");
        }
        return iOrderService.list(user.getId(), pageNum, pageSize);

    }
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session, long orderNo){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user==null){
            return ServerResponse.responseByError("Please login first.");
        }
        return iOrderService.detail(user.getId(), orderNo);

    }

}
