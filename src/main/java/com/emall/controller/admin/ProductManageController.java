package com.emall.controller.admin;

import com.emall.common.CONST;
import com.emall.common.ServerResponse;
import com.emall.pojo.Product;
import com.emall.pojo.User;
import com.emall.service.ICategoryService;
import com.emall.service.IProductService;
import com.emall.service.IUserService;
import com.google.common.collect.Maps;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;
    @Autowired
    private IProductService iProductService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse saveOrUpdateProduct(HttpSession session, Product product){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user==null){
            return ServerResponse.responseByError("Please login first.");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.responseByError("Please login as admin.");
        }
        return iProductService.saveOrUpdateProduct(product);
    }
    @RequestMapping("set_sale_status")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user==null){
            return ServerResponse.responseByError("Please login first.");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.responseByError("Please login as admin.");
        }
        return iProductService.setSaleStatus(productId, status);
    }
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user==null){
            return ServerResponse.responseByError("Please login first.");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.responseByError("Please login as admin.");
        }
        return iProductService.detail(productId);
    }
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,@RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user==null){
            return ServerResponse.responseByError("Please login first.");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.responseByError("Please login as admin.");
        }
        return iProductService.list(pageNum, pageSize);
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse search(HttpSession session, String keyword, Integer productId, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,@RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user==null){
            return ServerResponse.responseByError("Please login first.");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.responseByError("Please login as admin.");
        }
        return iProductService.searchKeywordAndProductId(keyword, productId, pageNum, pageSize);
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpServletRequest request, HttpSession session, @RequestParam("upload_file") MultipartFile file){
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user==null){
            return ServerResponse.responseByError("Please login first.");
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.responseByError("Please login as admin.");
        }
        //get the absolute path of folder "upload" in the "web" folder
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iProductService.upload(file, path);
    }
    //rich text upload required format of return value
//    {
//        success: false/true,
//        msg:"",
//        file_path:
//    }
    @RequestMapping("richtext_image_upload.do")
    @ResponseBody
    public Map richtextUpload(HttpServletRequest request, HttpSession session, @RequestParam("upload_file") MultipartFile file){
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(CONST.CURRENT_USER);
        if(user==null){
            resultMap.put("success", false);
            resultMap.put("msg", "Please login ");
            return resultMap;
        }
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            resultMap.put("success", false);
            resultMap.put("msg", "Please login as admin");
            return resultMap;
        }
        //get the absolute path of folder "upload" in the "web" folder
        String path = request.getSession().getServletContext().getRealPath("upload");
        resultMap.put("success", true);
        resultMap.put("msg", "Upload success");
        resultMap.put("file_path", iProductService.upload(file, path).getData());
        return resultMap;
    }
}
