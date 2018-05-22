package com.emall.util;

import java.math.BigDecimal;

public class PriceCalcUtil {
    private PriceCalcUtil(){

    }

    public static BigDecimal add(Double a, Double b){
        BigDecimal m = new BigDecimal(a.toString());
        BigDecimal n = new BigDecimal(b.toString());
       return m.add(n);
    }
    public static BigDecimal minus(Double a, Double b){
        BigDecimal m = new BigDecimal(a.toString());
        BigDecimal n = new BigDecimal(b.toString());
        return m.subtract(n);
    }
    public static BigDecimal multi(Double a, Double b){
        BigDecimal m = new BigDecimal(a.toString());
        BigDecimal n = new BigDecimal(b.toString());
        return m.multiply(n);
    }
    public static BigDecimal divide(Double a, Double b){
        BigDecimal m = new BigDecimal(a.toString());
        BigDecimal n = new BigDecimal(b.toString());
        return m.divide(n);
    }
}
