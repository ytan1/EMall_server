package com.emall.controller.admin;

import com.emall.common.CONST;
import com.emall.common.ServerResponse;
import com.emall.pojo.Category;
import com.emall.pojo.User;
import com.emall.service.ICategoryService;
import com.emall.service.IUserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse<String> addCategory(HttpSession session, @RequestParam("categoryName") String categoryName, @RequestParam(value = "parentId", defaultValue="0") int parentId){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user==null){
            return ServerResponse.responseByError("Please login first.");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.responseByError("Please login as admin.");
        }
        return iCategoryService.addCategory(categoryName, parentId);
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse<String> setCategory(HttpSession session, @RequestParam("categoryName") String categoryName, @RequestParam(value = "categoryId") int categoryId){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user==null){
            return ServerResponse.responseByError("Please login first.");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.responseByError("Please login as admin.");
        }
        return iCategoryService.setCategoryName(categoryName, categoryId);
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getParallelChildrenByParentId(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") int categoryId){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user==null){
            return ServerResponse.responseByError("Please login first.");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.responseByError("Please login as admin.");
        }
        return iCategoryService.getParallelChildrenByParentId(categoryId);
    }

    @RequestMapping("get_category_deep.do")
    @ResponseBody
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") int categoryId){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user==null){
            return ServerResponse.responseByError("Please login first.");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.responseByError("Please login as admin.");
        }
        return iCategoryService.selectCategoryAndChildrenById(categoryId);
    }
}


