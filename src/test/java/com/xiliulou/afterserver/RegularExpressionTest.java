package com.xiliulou.afterserver;

import java.util.regex.Pattern;

import java.util.regex.Matcher;

public class RegularExpressionTest {
    public static void main(String[] args) {
        String input = "Hello;12121$World";
        System.out.println(isContainNonAlphanumeric(input));
    }

    public static boolean isContainNonAlphanumeric(String input) {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9;]");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }
}
