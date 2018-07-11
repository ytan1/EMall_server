package com.emall.service;

import com.emall.common.ServerResponse;
import com.emall.dao.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;

public interface IOrderService {

    public ServerResponse createOrder(Integer userId, Integer shippingId);
    public ServerResponse cancelOrder(Integer userId, long orderNo);
    public ServerResponse getOrderCartProduct(Integer userId);
    public ServerResponse detail(Integer userId, long orderNo);
    public ServerResponse list(Integer userId, Integer pageNum, Integer pageSize);
    public ServerResponse manageList(Integer pageNum, Integer pageSize);
    public ServerResponse receive(Integer userId, long orderNo);


}
