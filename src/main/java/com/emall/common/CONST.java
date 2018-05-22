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
}
