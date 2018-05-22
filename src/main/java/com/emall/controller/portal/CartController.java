package com.emall.controller.portal;

import com.emall.common.CONST;
import com.emall.common.ResponseCode;
import com.emall.common.ServerResponse;
import com.emall.pojo.User;
import com.emall.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {


    @Autowired
    private ICartService iCartService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Integer productId, @RequestParam(value = "count", defaultValue = "0") Integer count) {
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if (user == null) {
            return ServerResponse.responseByError(ResponseCode.LOGIN.getCode(),"Please login first.");
        }
        return iCartService.add(productId, user.getId(), count);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session) {
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if (user == null) {
            return ServerResponse.responseByError(ResponseCode.LOGIN.getCode(),"Please login first.");
        }
        return iCartService.list(user.getId());
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session, Integer productId, Integer count) {
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if (user == null) {
            return ServerResponse.responseByError(ResponseCode.LOGIN.getCode(),"Please login first.");
        }
        return iCartService.update(productId, user.getId(), count);
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse delete(HttpSession session, String productIds) {
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if (user == null) {
            return ServerResponse.responseByError(ResponseCode.LOGIN.getCode(),"Please login first.");
        }
        return iCartService.delete(productIds, user.getId());
    }

    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse selectAll(HttpSession session) {
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if (user == null) {
            return ServerResponse.responseByError(ResponseCode.LOGIN.getCode(),"Please login first.");
        }
        return iCartService.selectOrUnselect(null, user.getId(), CONST.CHECKED.IS_CHECKED);
    }
    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse unSelectAll(HttpSession session) {
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if (user == null) {
            return ServerResponse.responseByError(ResponseCode.LOGIN.getCode(),"Please login first.");
        }
        return iCartService.selectOrUnselect(null, user.getId(), CONST.CHECKED.IS_NOT_CHECKED);
    }
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse selectOne(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if (user == null) {
            return ServerResponse.responseByError(ResponseCode.LOGIN.getCode(),"Please login first.");
        }
        return iCartService.selectOrUnselect(productId, user.getId(), CONST.CHECKED.IS_CHECKED);
    }
    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse selectAll(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if (user == null) {
            return ServerResponse.responseByError(ResponseCode.LOGIN.getCode(),"Please login first.");
        }
        return iCartService.selectOrUnselect(productId, user.getId(), CONST.CHECKED.IS_NOT_CHECKED);
    }

    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartCount(HttpSession session) {
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if (user == null) {
            return ServerResponse.responseBySuccess(0);
        }
        return iCartService.getCartCount(user.getId());
    }

}
