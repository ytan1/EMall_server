package com.emall.service.impl;

import com.emall.common.CONST;
import com.emall.common.ResponseCode;
import com.emall.common.ServerResponse;
import com.emall.dao.CategoryMapper;
import com.emall.dao.ProductMapper;
import com.emall.pojo.Category;
import com.emall.pojo.Product;
import com.emall.service.ICategoryService;
import com.emall.service.IProductService;
import com.emall.util.FTPUtil;
import com.emall.util.PropertyUtil;
import com.emall.vo.ProductDetailVO;
import com.emall.vo.ProductSimpleVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("iProductService")
public class productServiceImplement implements IProductService {

    private Logger logger = LoggerFactory.getLogger(productServiceImplement.class);

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;

    public ServerResponse saveOrUpdateProduct(Product product){
        if(product == null){
            return ServerResponse.responseByError(ResponseCode.ILLEGAL_ARGS.getCode(), ResponseCode.ILLEGAL_ARGS.getMsg());
        }
        //add mainImage property if necessary
        if(StringUtils.isBlank(product.getMainImage())){
            String[] subImages = product.getSubImages().split(",");
            if(subImages.length>0){
                product.setMainImage(subImages[0]);
            }
        }
        //check if the item exists in db, and then update or insert
       Product selectResult =  productMapper.selectByPrimaryKey(product.getId());
       if(selectResult!=null){
           //update
            int updateResult = productMapper.updateByPrimaryKeySelective(product);
            if(updateResult > 0){
                return ServerResponse.responseBySuccessMessage("Update product success.");
            }else{
                return ServerResponse.responseByError("Update product fails.");
            }
       }else{
           //insert
           int insertResult = productMapper.insertSelective(product);
           if(insertResult>0){
               return ServerResponse.responseBySuccessMessage("Create product success.");
           }else{
               return ServerResponse.responseByError("Create product fails.");
           }
       }

    }

    public ServerResponse setSaleStatus(Integer productId, Integer status){
        if(productId==null || status == null){
            return ServerResponse.responseByError(ResponseCode.ILLEGAL_ARGS.getCode(), ResponseCode.ILLEGAL_ARGS.getMsg());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        //update selective
        int updateResult = productMapper.updateByPrimaryKeySelective(product);
        if(updateResult>0){
            return ServerResponse.responseBySuccessMessage("Update status success.");
        }else{
            return ServerResponse.responseByError("Update status fails.");
        }
    }

    public ServerResponse<ProductDetailVO> detail(Integer productId){
        if(productId==null ){
            return ServerResponse.responseByError(ResponseCode.ILLEGAL_ARGS.getCode(), ResponseCode.ILLEGAL_ARGS.getMsg());
        }
        Product selectResult = productMapper.selectByPrimaryKey(productId);
        if(selectResult==null){
            ServerResponse.responseByError("There is so such product with the Id provided.");
        }
        ProductDetailVO vo = this.assembleProductDetailVO(selectResult);
        return ServerResponse.responseBySuccess("Get detail success.", vo);

    }
    private ProductDetailVO assembleProductDetailVO(Product product){
        ProductDetailVO vo = new ProductDetailVO();
        vo.setId(product.getId());
        vo.setCategoryId(product.getCategoryId());
        vo.setDetail(product.getDetail());
        vo.setMainImage(product.getMainImage());
        vo.setName(product.getName());
        vo.setPrice(product.getPrice());
        vo.setStatus(product.getStatus());
        vo.setStock(product.getStock());
        vo.setSubImages(product.getSubImages());
        vo.setSubTitle(product.getSubImages());
        //properties util to set host image, actually can use String diretly...
        vo.setImageHost(PropertyUtil.getValue("ftp.server.http.prefix"));
        //Category if not exist, set parent category 0
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category==null){
            vo.setParentCategoryId(0);
        }else{
            vo.setParentCategoryId(category.getParentId());
        }
        //todo format date to string
        vo.setCreateTime(product.getCreateTime());
        vo.setUpdateTime(product.getUpdateTime());
        return vo;
    }

    public ServerResponse<PageInfo> list(Integer pageNum, Integer pageSize){
        //use pagehelper plugin to devide list
        PageHelper.startPage(pageNum, pageSize);
        //select all products
        List<Product> products = productMapper.selectList();
        //assemble to productSimpleVO
        List<ProductSimpleVO> listVO = Lists.newArrayList();
        for(Product item: products){
            listVO.add(assembleProductSimpleVO(item));
        }
        //PageInfo
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(listVO);
        return ServerResponse.responseBySuccess("List of all products.", pageInfo);
    }
    private ProductSimpleVO assembleProductSimpleVO(Product product){
        ProductSimpleVO vo = new ProductSimpleVO();

        vo.setId(product.getId());
        vo.setCategoryId(product.getCategoryId());
        vo.setMainImage(product.getMainImage());
        vo.setName(product.getName());
        vo.setPrice(product.getPrice());
        vo.setStatus(product.getStatus());
        vo.setSubTitle(product.getSubImages());
        //properties util to set host image, actually can use String diretly...
        vo.setImageHost(PropertyUtil.getValue("ftp.server.http.prefix"));
        return vo;
    }
    public ServerResponse<PageInfo> searchKeywordAndProductId(String keyword, Integer productId, Integer pageNum, Integer pageSize){
        //use pagehelper plugin to devide list
        PageHelper.startPage(pageNum, pageSize);
        //select products by keyword or productId
        List<Product> products = productMapper.searchByKeywordAndProductId(keyword, productId);
        //assemble to productSimpleVO
        List<ProductSimpleVO> listVO = Lists.newArrayList();
        for(Product item: products){
            listVO.add(assembleProductSimpleVO(item));
        }
        //PageInfo
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(listVO);
        return ServerResponse.responseBySuccess("List of all products.", pageInfo);
    }
    public ServerResponse upload(MultipartFile file, String path){
        boolean result = false;
        //generate a unique name of file in upload folder
        String oldName = file.getOriginalFilename();
        String ext = oldName.substring(oldName.lastIndexOf(".")+1);
        String newName = UUID.randomUUID().toString() + "." + ext;
        //create the directory on Tomcat server disk
        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        //write the file on disk(temporarily)
        File fileOnDisk = new File(path, newName);
        try{
            //write file on the tomcat server disk;
            file.transferTo(fileOnDisk);
            logger.info("write file on tomcat server disk");
            //upload to ftp server
            result = FTPUtil.upload(fileOnDisk);

            fileOnDisk.delete();

        } catch (IOException e) {
            logger.error("Upload error", e);
            e.printStackTrace();
        }

        if(result){
            Map resultMap = Maps.newHashMap();
            String url = PropertyUtil.getValue("ftp.server.http.prefix")+ newName;
            resultMap.put("url", url);
            resultMap.put("uri", newName);
            return ServerResponse.responseBySuccess("Upload success", resultMap);
        }else{
            return ServerResponse.responseByError("Upload fail");
        }
    }
    public ServerResponse searchByKeywordAndCategoryId(String keyword, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy){

        List<Integer> categoryIds = Lists.newArrayList();
        if(categoryId!=null){

            categoryIds = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();

        }
//        PageHelper pageHelper = new PageHelper();
        PageHelper.startPage(pageNum,pageSize);
        if(orderBy.equals(CONST.ORDER.ASC)){
            PageHelper.orderBy("price asc");
        }else if(orderBy.equals(CONST.ORDER.DESC)){
            PageHelper.orderBy("price desc");
        }

        List<Product> products= Lists.newArrayList();
        List<ProductSimpleVO> listVO= Lists.newArrayList();

        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        products = productMapper.searchByKeywordAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword, categoryIds.size()==0?null:categoryIds);
        for(Product product: products){
            listVO.add(this.assembleProductSimpleVO(product));
        }

        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(listVO);

        return ServerResponse.responseBySuccess(pageInfo);

    }
}
