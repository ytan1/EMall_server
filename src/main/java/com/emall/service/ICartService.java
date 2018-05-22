package com.emall.service;

import com.emall.common.ServerResponse;
import com.emall.vo.CartVO;
import org.springframework.web.bind.annotation.RequestParam;

public interface ICartService {
    public ServerResponse add(Integer productId, Integer userId, Integer count);
    public ServerResponse list(Integer userId);
    public ServerResponse update(Integer productId, Integer userId, Integer count);
    public ServerResponse<CartVO> delete(String productIds, Integer userId);
    public ServerResponse<CartVO> selectOrUnselect(Integer productId, Integer userId, Integer isSelected);
    public ServerResponse<Integer> getCartCount(Integer userId);
}
