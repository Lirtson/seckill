package com.lirtson.seckill.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;


public class JWTUtils {
    public static String JWT_SECRET="43FHJRE8349020EHY";
    public static String createJWT(Map claims, String subject, long ttlMillis) throws Exception{
        //指定签名算法
        SignatureAlgorithm signatureAlgorithm=SignatureAlgorithm.HS256;
        //生成时间
        long nowMillis=System.currentTimeMillis();
        Date now=new Date(nowMillis);
        //生成签名时使用的密钥
        SecretKey key=generalKey();
        //为payload添加各种标准声明和私有声明
        JwtBuilder builder=Jwts.builder()
                .setClaims(claims)//如果有私有声明，要先私有声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setIssuedAt(now)//jwt的签发时间
                .setSubject(subject)//json字符串作为用户的唯一标志
                .signWith(signatureAlgorithm,key);//设置签名使用的签名算法和签名使用的密钥
        if(ttlMillis>=0){
            long expMillis=nowMillis+ttlMillis;
            Date exp=new Date(expMillis);
            builder.setExpiration(exp);//设置过期时间戳
        }
        return builder.compact();
    }

    private static SecretKey generalKey(){
        String stringKey=JWT_SECRET;
        //使用base64解码
        byte[] encodeKey=Base64.decodeBase64(stringKey);
        SecretKey secretKey=new SecretKeySpec(encodeKey,0,encodeKey.length,"AES");
        return secretKey;
    }

    public static Claims parseJWT(String jwt) throws Exception{
        //得到原来的签名秘钥，用其才能解析JWT
        SecretKey key=generalKey();
        //得到 DefaultJwtParser
        Claims claims= Jwts.parser()
                .setSigningKey(key)//设置签名的秘钥
                .parseClaimsJws(jwt).getBody();//设置需要解析的jwt
        return claims;
    }

}
