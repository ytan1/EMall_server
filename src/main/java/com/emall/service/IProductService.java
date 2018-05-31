package com.emall.service;

import com.emall.common.ServerResponse;
import com.emall.pojo.Product;
import com.emall.vo.ProductDetailVO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

public interface IProductService {
    public ServerResponse saveOrUpdateProduct(Product product);
    public ServerResponse setSaleStatus(Integer productId, Integer status);
    public ServerResponse<ProductDetailVO> detail(Integer productId);
    public ServerResponse<PageInfo> list(Integer pageNum, Integer pageSize);
    public ServerResponse<PageInfo> searchKeywordAndProductId(String keyword, Integer productId, Integer pageNum, Integer pageSize);
    public ServerResponse upload(MultipartFile file, String path);
    public ServerResponse searchByKeywordAndCategoryId(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy);
    public ServerResponse<ProductDetailVO> detailForCustomer(Integer productId);
}
