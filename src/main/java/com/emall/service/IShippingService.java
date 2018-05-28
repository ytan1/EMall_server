package com.emall.service;

import com.emall.common.ServerResponse;
import com.emall.pojo.Shipping;

import java.util.Map;

public interface IShippingService {
    public ServerResponse<Map> add(Integer userId, Shipping shipping);
    public ServerResponse delete(Integer userId, Integer shippingId);
    public ServerResponse update(Integer userId, Shipping shipping);
    public ServerResponse select(Integer userId, Integer shippingId);
    public ServerResponse list(Integer userId, Integer pageNum, Integer pageSize);
}
