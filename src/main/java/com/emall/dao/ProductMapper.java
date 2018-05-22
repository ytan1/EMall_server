package com.emall.dao;

import com.emall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectList();

    List<Product> searchByKeywordAndProductId(@Param("keyword") String keyword, @Param("productId") Integer productId);

    List<Product> searchByKeywordAndCategoryIds(@Param("keyword") String keyword, @Param("categoryIds") List<Integer> categoryIds);
}