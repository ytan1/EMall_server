package com.emall.service.impl;

import com.emall.common.CONST;
import com.emall.common.ResponseCode;
import com.emall.common.ServerResponse;
import com.emall.dao.*;
import com.emall.pojo.*;
import com.emall.service.IOrderService;
import com.emall.util.PriceCalcUtil;
import com.emall.util.PropertyUtil;
import com.emall.vo.OrderItemVO;
import com.emall.vo.OrderProductVO;
import com.emall.vo.OrderVO;
import com.emall.vo.ShippingVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.sun.org.apache.bcel.internal.classfile.PMGClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.x509.OIDMap;

import javax.imageio.spi.ServiceRegistry;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service("iOrderService")
public class orderServiceImplement implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse createOrder(Integer userId, Integer shippingId){
        List<OrderItem> orderItemList = Lists.newArrayList();
        List<Cart> cartList = Lists.newArrayList();
        cartList = cartMapper.selectByUserId(userId);

        //convert items in cart to items in order , need to check if it is checked in cart and on sale
        ServerResponse response = this.assembleOrderItemList(userId, cartList);
        if(!response.isSuccess()){
            return response;
        }
        orderItemList = (List<OrderItem>) response.getData();
        if(orderItemList.size()==0){
            return ServerResponse.responseByError("No item in cart yet.");
        }
        //get total payment from the order
        BigDecimal payment = this.calcPayment(orderItemList);
        //create Order object
        Order order = this.assembleOrder(userId, shippingId, payment);
        //set orderNo for orderItem also and insert to db
        for(OrderItem orderItem: orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
            int result = orderItemMapper.insert(orderItem);
            if(result == 0){
                return ServerResponse.responseByError("Order item inserted fail in order " + order.getOrderNo());
            }
        }
        //reduct the stock quantity
        this.reduceStockQuantity(orderItemList);
        //empty the cart, notice even the cart item is not checked it will be emptied
        this.emptyCart(cartList);

//        orderMapper.insert(order);  //insert in assembleorder

        OrderVO orderVO = this.assembleOrderVO(order, orderItemList);

        return ServerResponse.responseBySuccess(orderVO);

    }

    private ServerResponse assembleOrderItemList(Integer userId, List<Cart> cartList){
        List<OrderItem> orderItemList = Lists.newArrayList();

        if(cartList.size()==0){
            return ServerResponse.responseByError("You have no items in cart.");
        }


        for(Cart cart: cartList){
            if(cart.getChecked() == CONST.CHECKED.IS_CHECKED) {
                OrderItem orderItem = new OrderItem();
                orderItem.setUserId(userId);
                orderItem.setProductId(cart.getProductId());
                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if (product.getStatus() != 1) {
                    return ServerResponse.responseByError("Product" + product.getName() + "is not on sale.");
                }
                orderItem.setProductName(product.getName());
                orderItem.setProductImage(product.getMainImage());
                orderItem.setCurrentUnitPrice(product.getPrice());
                if (cart.getQuantity() < product.getStock()) {
                    orderItem.setQuantity(cart.getQuantity());
                    orderItem.setTotalPrice(PriceCalcUtil.multi(orderItem.getCurrentUnitPrice().doubleValue(), orderItem.getQuantity().doubleValue()));
                } else {
                    return ServerResponse.responseByError("Product" + product.getName() + "is not enough in stock.");
                }
                orderItemList.add(orderItem);
            }
        }
        return ServerResponse.responseBySuccess(orderItemList);
    }

    private BigDecimal calcPayment(List<OrderItem> orderItemList){
        BigDecimal payment = new BigDecimal("0");
        for(OrderItem orderItem: orderItemList){
            payment = PriceCalcUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }
    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment){
        Order order = new Order() ;
        long orderNo = this.generateOrderNo();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPayment(payment);
        order.setPaymentType(1); // 1 for online, only online option for the moment
        order.setPostage(0);  // this project doesn't consider postage, it's all free!
        order.setStatus(CONST.OrderStatusEnum.NO_PAY.getCode());

        //finally insert order in to mapper
        int resultCount = orderMapper.insert(order);
        if(resultCount == 0){
            return null;
        }
        return order;
    }
    private long generateOrderNo(){
        long currentTime= System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }
    private void reduceStockQuantity(List<OrderItem> orderItems){
        for(OrderItem orderItem: orderItems){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }
    private void emptyCart(List<Cart> carts){
        for (Cart cart: carts){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }
    private OrderVO assembleOrderVO(Order order, List<OrderItem> orderItemList){
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderNo(order.getOrderNo());
        orderVO.setPayment(order.getPayment());

        orderVO.setPaymentTypeDesc("Online");
        orderVO.setPostage(order.getPostage());
        orderVO.setStatus(order.getStatus());
        orderVO.setStatusDesc(CONST.OrderStatusEnum.getOrderStatusEnumByCode(order.getStatus()));
        orderVO.setPaymentTime(order.getPaymentTime());
        orderVO.setSendTime(order.getSendTime());
        orderVO.setEndTime(order.getEndTime());
        orderVO.setCloseTime(order.getCloseTime());
        orderVO.setCreateTime(order.getCreateTime());
        orderVO.setUpdateTime(order.getUpdateTime());

        List<OrderItemVO> orderItemVOList = Lists.newArrayList();
        for(OrderItem orderItem: orderItemList){
            orderItemVOList.add(this.assembleOrderItemVO(orderItem));
        }
        orderVO.setOrderItemVoList(orderItemVOList);
        Integer shippingId = order.getShippingId();
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        if(shipping != null){ // always check null after sql!!!
            orderVO.setShippingId(shippingId);
            orderVO.setShippingVO(this.assembleShippingVO(shipping));
            orderVO.setReceiverName(shipping.getReceiverName());
        }

        orderVO.setImageHost(PropertyUtil.getValue("ftp.server.http.prefix"));

        return orderVO;


    }
    private OrderItemVO assembleOrderItemVO(OrderItem orderItem) {
        OrderItemVO orderItemVO = new OrderItemVO();
        orderItemVO.setOrderNo(orderItem.getOrderNo());
        orderItemVO.setProductId(orderItem.getProductId());
        orderItemVO.setProductName(orderItem.getProductName());
        orderItemVO.setProductImage(orderItem.getProductImage());
        orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVO.setQuantity(orderItem.getQuantity());
        orderItemVO.setTotalPrice(orderItem.getTotalPrice());
        orderItemVO.setCreateTime(orderItem.getCreateTime());

        return orderItemVO;

    }
    private ShippingVO assembleShippingVO(Shipping shipping){
        ShippingVO shippingVO = new ShippingVO();
        shippingVO.setReceiverName(shipping.getReceiverName());
        shippingVO.setReceiverAddress(shipping.getReceiverAddress());
        shippingVO.setReceiverProvince(shipping.getReceiverProvince());
        shippingVO.setReceiverCity(shipping.getReceiverCity());
        shippingVO.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVO.setReceiverMobile(shipping.getReceiverMobile());
        shippingVO.setReceiverZip(shipping.getReceiverZip());
        shippingVO.setReceiverPhone(shipping.getReceiverPhone());
        return shippingVO;
    }



    //cancle the order according userid and orderNo
    public ServerResponse cancelOrder(Integer userId, long orderNo){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if(order == null){
            return ServerResponse.responseByError("The user has no such order.");
        }
        if(order.getStatus() != CONST.OrderStatusEnum.NO_PAY.getCode()){
            return ServerResponse.responseByError("Already paid, can not cancel the order.");
        }
        //change the status code to cancle
        Order orderNew = new Order();
        orderNew.setStatus(CONST.OrderStatusEnum.CANCELED.getCode());
        orderNew.setId(order.getId());
        int result = orderMapper.updateByPrimaryKeySelective(orderNew);
        if(result == 0){
            return ServerResponse.responseByError("Update order fails.");
        }
        return ServerResponse.responseBySuccessMessage("Update order success.");
    }

    //show checked product in the cart in order page before creating the order
    public ServerResponse getOrderCartProduct(Integer userId){
        List<OrderItem> orderItemList = Lists.newArrayList();
        List<OrderItemVO> orderItemVOList = Lists.newArrayList();
        List<Cart> cartList = Lists.newArrayList();
        cartList = cartMapper.selectByUserId(userId);

        //convert items in cart to items in order , need to check if it is checked in cart and on sale
        ServerResponse response = this.assembleOrderItemList(userId, cartList);
        if(!response.isSuccess()){
            return response;
        }
        orderItemList = (List<OrderItem>) response.getData();
        if(orderItemList.size()==0){
            return ServerResponse.responseByError("No item in cart yet.");
        }
        for(OrderItem orderItem: orderItemList){
            OrderItemVO orderItemVO = this.assembleOrderItemVO(orderItem);
            orderItemVOList.add(orderItemVO);
        }

        //get total payment from the order
        BigDecimal payment = this.calcPayment(orderItemList);
        OrderProductVO orderProductVO = new OrderProductVO();
        orderProductVO.setImageHost(PropertyUtil.getValue("ftp.server.http.prefix"));
        orderProductVO.setOrderItemVoList(orderItemVOList);
        orderProductVO.setProductTotalPrice(payment);

        return ServerResponse.responseBySuccess(orderProductVO);
    }

    public ServerResponse detail(Integer userId, long orderNo){
        if(userId==null){
            return ServerResponse.responseByError(ResponseCode.ILLEGAL_ARGS.getCode(),ResponseCode.ILLEGAL_ARGS.getMsg());
        }
        //select with userId from session for security reason
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if(order==null){
            return ServerResponse.responseByError("The user has no such order");
        }

        List<OrderItem> orderItemList = Lists.newArrayList();
        orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId, orderNo);
        OrderVO orderVO = this.assembleOrderVO(order, orderItemList);
        return ServerResponse.responseBySuccess(orderVO);
    }

    public ServerResponse list(Integer userId, Integer pageNum, Integer pageSize){
        if(userId==null){
            return ServerResponse.responseByError(ResponseCode.ILLEGAL_ARGS.getCode(),ResponseCode.ILLEGAL_ARGS.getMsg());
        }
        List<Order> orderList = Lists.newArrayList();
        List<OrderVO> orderVOList = Lists.newArrayList();
        List<OrderItem> orderItemList = Lists.newArrayList();
        PageHelper.startPage(pageNum, pageSize);
        orderList = orderMapper.selectAllByUserId(userId);
        if(orderList.size()==0){
            return ServerResponse.responseByError("You have no order yet.");
        }

        for(Order order: orderList){
            orderItemList = orderItemMapper.selectByUserIdAndOrderNo(userId, order.getOrderNo());
            OrderVO orderVO = this.assembleOrderVO(order, orderItemList);
            orderVOList.add(orderVO);
        }
        //pagination info

        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVOList);

        return ServerResponse.responseBySuccess(pageInfo);

    }
    public ServerResponse manageList(Integer pageNum, Integer pageSize){
        List<Order> orderList = Lists.newArrayList();
        List<OrderVO> orderVOList = Lists.newArrayList();
        List<OrderItem> orderItemList = Lists.newArrayList();
        PageHelper.startPage(pageNum, pageSize);
        orderList = orderMapper.selectAll();
        if(orderList.size()==0){
            return ServerResponse.responseByError("You have no order yet.");
        }

        for(Order order: orderList){
            orderItemList = orderItemMapper.selectByUserIdAndOrderNo(order.getUserId(), order.getOrderNo());
            OrderVO orderVO = this.assembleOrderVO(order, orderItemList);
            orderVOList.add(orderVO);
        }
        //pagination info

        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVOList);

        return ServerResponse.responseBySuccess(pageInfo);
    }
}
