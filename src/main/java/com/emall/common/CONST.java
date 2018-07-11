package com.emall.common;

public class CONST {
    public static final String CURRENT_USER = "current_user";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public interface ROLE{
        public int CUSTOMER = 0;
        public int ADMIN = 1;
    }
    public interface ORDER{
        public String ASC = "price_asc";
        public String DESC = "price_desc";
    }
    public interface CHECKED{
        public int IS_CHECKED = 1;
        public int IS_NOT_CHECKED = 0;
    }

    public enum OrderStatusEnum{
        CANCELED(0,"Canceled"),
        NO_PAY(10,"Not paid"),
        PAID(20,"Paid");
//        SHIPPED(40,"Sent"),
//        ORDER_SUCCESS(50,"Order done"),
//        ORDER_CLOSE(60,"Order closed");


        OrderStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static String getOrderStatusEnumByCode(int code){
            for(OrderStatusEnum orderStatusEnum: values()){
                if(orderStatusEnum.getCode() == code){
                    return orderStatusEnum.getValue();
                }
            }
            return null;
        }


    }
}
