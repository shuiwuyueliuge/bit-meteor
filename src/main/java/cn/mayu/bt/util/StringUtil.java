package cn.mayu.bt.util;

import java.util.Random;

/**
 *
 */
public class StringUtil {

    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789~!@#$%^&*()_+{}";
        Random random=new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }

        return sb.toString();
    }
}
