package com.baizhang.bmeko.util;

import io.jsonwebtoken.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author YONGHONG ZHANG
 * @date 2019-06-01-14:28
 */
public class JwtUtil {

    public static void main(String[] args){

        String salt = "127.0.0.1";
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("userId","2");
        stringStringHashMap.put("nickName","博哥");

        String token = encode("baizhang", stringStringHashMap, salt);
        System.out.println(token);


        Map userMap = decode("baizhang", token, salt);

        System.out.println(userMap);

    }


    /***
     * jwt加密
     * @param key
     * @param map
     * @param salt
     * @return
     */
    public static String encode(String key,Map map,String salt){

        if(salt!=null){
            key+=salt;
        }
        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256, key);
        jwtBuilder.addClaims(map);

        String token = jwtBuilder.compact();
        return token;
    }

    /***
     * jwt解密
     * @param key
     * @param token
     * @param salt
     * @return
     * @throws SignatureException
     */
    public static  Map decode(String key,String token,String salt)throws SignatureException {
        if(salt!=null){
            key+=salt;
        }
        Claims map = null;

        map = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();

        System.out.println("map.toString() = " + map.toString());

        return map;

    }

}
