package com.emall.service.impl;

import com.emall.common.ResponseCode;
import com.emall.common.ServerResponse;
import com.emall.dao.ShippingMapper;
import com.emall.pojo.Shipping;
import com.emall.service.IShippingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service("iShippingService")
public class shippingServiceImplement implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse<Map> add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);

        int resultCount = shippingMapper.insert(shipping);
        if(resultCount>0){
            Map result = Maps.newHashMap();
            result.put("shippingId", shipping.getId());
            return ServerResponse.responseBySuccess("Add address success.", result);
        }
        return ServerResponse.responseByError("Add address fail.");
    }

    public ServerResponse delete(Integer userId, Integer shippingId){
        if(shippingId == null || userId == null){
            return ServerResponse.responseByError(ResponseCode.ILLEGAL_ARGS.getCode(), ResponseCode.ILLEGAL_ARGS.getMsg());
        }
        int resultCount = shippingMapper.deleteByIdAndUserId(shippingId, userId);
        if(resultCount > 0 ){
            return ServerResponse.responseBySuccess("Delete address success.");
        }
        return ServerResponse.responseByError("Delete address fail.");
    }

    public ServerResponse update(Integer userId, Shipping shipping){
        if(shipping.getId() == null || userId == null){
            return ServerResponse.responseByError(ResponseCode.ILLEGAL_ARGS.getCode(), ResponseCode.ILLEGAL_ARGS.getMsg());
        }
        shipping.setUserId(userId);
        int result = shippingMapper.updateByUserId(shipping);
        if (result > 0) {

            return ServerResponse.responseBySuccessMessage("Update success.");
        }

        return  ServerResponse.responseByError("Update fails.");

    }

    public ServerResponse select(Integer userId, Integer shippingId){
        if(shippingId == null || userId == null){
            return ServerResponse.responseByError(ResponseCode.ILLEGAL_ARGS.getCode(), ResponseCode.ILLEGAL_ARGS.getMsg());
        }

        Shipping shipping = shippingMapper.selectByUserId(shippingId, userId);

        return ServerResponse.responseBySuccess(shipping);
    }

    public ServerResponse list(Integer userId, Integer pageNum, Integer pageSize){
        if(userId == null){
            return ServerResponse.responseByError(ResponseCode.ILLEGAL_ARGS.getCode(), ResponseCode.ILLEGAL_ARGS.getMsg());
        }


        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectAllByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);

        return ServerResponse.responseBySuccess("List select success", pageInfo);

    }
}
