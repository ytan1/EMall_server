package com.emall.dao;

import com.emall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByProductIdAndUserId(@Param("productId") Integer productId, @Param("userId")Integer userId);

    List<Cart> selectByUserId(Integer userId);

    int isAllChecked(Integer userId);

    int deleteByUserIdAndProductIds(@Param("userId") Integer userId, @Param("productIdList")List<Integer> productIdList);

    int updateSelect(@Param("productId")Integer productId, @Param("userId") Integer userId, @Param("isSelected") Integer isSelected);

    Integer getCartProductCount(Integer userId);
}
