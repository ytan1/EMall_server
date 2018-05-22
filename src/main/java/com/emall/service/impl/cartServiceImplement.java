package com.emall.service.impl;

import com.emall.common.CONST;
import com.emall.common.ResponseCode;
import com.emall.common.ServerResponse;
import com.emall.dao.CartMapper;
import com.emall.dao.ProductMapper;
import com.emall.pojo.Cart;
import com.emall.pojo.Product;
import com.emall.service.ICartService;
import com.emall.util.PriceCalcUtil;
import com.emall.util.PropertyUtil;
import com.emall.vo.CartProductVO;
import com.emall.vo.CartVO;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service("iCartService")
public class cartServiceImplement implements ICartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    private static Logger logger = LoggerFactory.getLogger(cartServiceImplement.class);

    public ServerResponse add(Integer productId, Integer userId, Integer count){

        if(productId == null || userId == null ){
            return ServerResponse.responseByError(ResponseCode.ILLEGAL_ARGS.getCode(), ResponseCode.ILLEGAL_ARGS.getMsg());
        }
        Cart cart = cartMapper.selectByProductIdAndUserId(productId, userId);
        if(cart!=null){
            cart.setQuantity(cart.getQuantity()+count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }else{
            cart.setQuantity(count);
            cart.setChecked(CONST.CHECKED.IS_CHECKED);
            cart.setProductId(productId);
            cart.setUserId(userId);
            cartMapper.insert(cart);
        }

        CartVO cartVO = this.makeCartVO(userId);
        return ServerResponse.responseBySuccess(cartVO);
    }

    public ServerResponse list(Integer userId){
        CartVO cartVO = this.makeCartVO(userId);
        return ServerResponse.responseBySuccess(cartVO);
    }

    public ServerResponse update(Integer productId, Integer userId, Integer count){
        if(productId == null || userId == null || count == null){
            return ServerResponse.responseByError(ResponseCode.ILLEGAL_ARGS.getCode(), ResponseCode.ILLEGAL_ARGS.getMsg());
        }
        Cart cart = cartMapper.selectByProductIdAndUserId(productId, userId);
        if(cart!=null){
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }else{
            return ServerResponse.responseByError("Product not in cart.");
        }
        return this.list(userId);
    }

    public ServerResponse<CartVO> delete(String productIds, Integer userId){
        if(productIds == null || userId == null){
            return ServerResponse.responseByError(ResponseCode.ILLEGAL_ARGS.getCode(), ResponseCode.ILLEGAL_ARGS.getMsg());
        }

        String[] productIdArr = productIds.split(",");//productId separated by , from front page
        List<Integer> productIdList = Lists.newArrayList();
        for(int i = 0; i < productIdArr.length; i++){
            try {
                Integer productIdListItem = Integer.parseInt(productIdArr[i]);
                productIdList.add(productIdListItem);
            }catch(NumberFormatException e){
                logger.error("ProductIds not valid" ,e);
            }
        }
        //actually not necessary to convert String[] to List<Integer> for mapper??
        if(productIdList.size()==0){
            return ServerResponse.responseByError("No productId is valid.");
        }
        cartMapper.deleteByUserIdAndProductIds(userId, productIdList);
        return this.list(userId);
    }

    public ServerResponse<CartVO> selectOrUnselect(Integer productId, Integer userId, Integer isSelected){
        if(isSelected == null || userId == null){
            return ServerResponse.responseByError(ResponseCode.ILLEGAL_ARGS.getCode(), ResponseCode.ILLEGAL_ARGS.getMsg());
        }
        if(isSelected != CONST.CHECKED.IS_CHECKED && isSelected != CONST.CHECKED.IS_NOT_CHECKED){
            return ServerResponse.responseByError(ResponseCode.ILLEGAL_ARGS.getCode(), ResponseCode.ILLEGAL_ARGS.getMsg());
        }
        cartMapper.updateSelect(productId, userId, isSelected);
        return this.list(userId);
    }

    public ServerResponse<Integer> getCartCount(Integer userId){
        if(userId == null){
            return ServerResponse.responseByError(ResponseCode.ILLEGAL_ARGS.getCode(), ResponseCode.ILLEGAL_ARGS.getMsg());
        }
        Integer cartCount = cartMapper.getCartProductCount(userId);
        if(cartCount == null){
            return ServerResponse.responseBySuccess(0);
        }
        return  ServerResponse.responseBySuccess(cartCount);
    }

    private CartVO makeCartVO(Integer userId){
        List<Cart> cartList = Lists.newArrayList();
        List<CartProductVO> cartProductVOList = Lists.newArrayList();
        CartProductVO cartProductVO = new CartProductVO();
        BigDecimal totalPrice = new BigDecimal("0");
        cartList = cartMapper.selectByUserId(userId);

        if(cartList.size()!=0){
            for(Cart cartItem : cartList){
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product != null){
                    cartProductVO.setProductId(product.getId());
                    cartProductVO.setId(cartItem.getId());
                    cartProductVO.setProductMainImage(product.getMainImage());
                    cartProductVO.setProductName(product.getName());
                    cartProductVO.setProductPrice(product.getPrice());
                    cartProductVO.setProductStatus(product.getStatus());
                    int productStock = product.getStock();
                    cartProductVO.setProductStock(productStock);
                    cartProductVO.setProductSubtitle(product.getSubtitle());
                    cartProductVO.setProductChecked(cartItem.getChecked());
                    cartProductVO.setUserId(cartItem.getUserId());
                    int cartQuantity = cartItem.getQuantity();
                    if(cartQuantity > productStock){
                        cartProductVO.setLimitQuantity("LIMIT_NUM_FAIL");
                        cartQuantity = productStock;
                        cartProductVO.setQuantity(cartQuantity);
                        //update cart in db
                        Cart cartSet = new Cart();
                        cartSet.setId(cartProductVO.getId());
                        cartSet.setQuantity(cartProductVO.getQuantity());
                        cartMapper.updateByPrimaryKeySelective(cartSet);
                    }else{
                        cartProductVO.setLimitQuantity("LIMIT_NUM_SUCCESS");
                        cartProductVO.setQuantity(cartQuantity);
                    }
                    cartProductVO.setProductTotalPrice(PriceCalcUtil.multi(cartProductVO.getProductPrice().doubleValue(), cartProductVO.getQuantity().doubleValue()));
                }
                cartProductVOList.add(cartProductVO);
                if(cartProductVO.getProductChecked()==CONST.CHECKED.IS_CHECKED){
                    totalPrice = PriceCalcUtil.add(totalPrice.doubleValue(), cartProductVO.getProductTotalPrice().doubleValue());

                }

            }
        }
        CartVO cartVO = new CartVO();
        cartVO.setCartProductVoList(cartProductVOList);
        cartVO.setCartTotalPrice(totalPrice);
        cartVO.setImageHost(PropertyUtil.getValue("ftp.server.http.prefix"));
        cartVO.setCartAllChecked(this.isAllChecked(userId));

        return cartVO;
    }

    private boolean isAllChecked(Integer userId){
        if(userId == null){
            return false;
        }
        return cartMapper.isAllChecked(userId) == 0;
    }
}
