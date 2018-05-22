package com.emall.test;

import org.junit.Test;

import java.math.BigDecimal;

public class BigDecimalTest {
    @Test
    public void test1(){
        BigDecimal a = new BigDecimal(0.02);
        BigDecimal b = new BigDecimal("0.02");
        System.out.println(a.add(b));
    }
}
