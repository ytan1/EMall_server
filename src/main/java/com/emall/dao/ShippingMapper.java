package com.emall.dao;

import com.emall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByIdAndUserId(@Param("shippingId") Integer shippingId, @Param("userId") Integer userId);

    int updateByUserId(Shipping record);

    Shipping selectByUserId(@Param("shippingId") Integer shippingId, @Param("userId") Integer userId);
    List<Shipping> selectAllByUserId(@Param("userId") Integer userId);
}