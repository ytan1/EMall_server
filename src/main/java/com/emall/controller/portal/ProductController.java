package com.emall.controller.portal;

import com.emall.common.ServerResponse;
import com.emall.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detailForCustomer(Integer productId){
        return iProductService.detailForCustomer(productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(@RequestParam(value = "keyword" ,required = false) String keyword,
                                @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                @RequestParam(value = "orderBy", defaultValue = "default") String orderBy){
        return iProductService.searchByKeywordAndCategoryId(keyword, categoryId, pageNum,pageSize,orderBy);
    }
}
