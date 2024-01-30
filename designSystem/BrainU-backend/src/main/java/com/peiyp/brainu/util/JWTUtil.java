package com.peiyp.brainu.util;

import cn.hutool.crypto.digest.DigestUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;

/**
 * @author PeiYP
 * @since 2023年05月16日 16:43
 */
public class JWTUtil {
    /*
     * 获取token中的参数
     *
     * @param token
     * @return
     */
    public static Claims parseToken(String token,String key) {
        if ("".equals(token)) {
            return null;
        }
        try {
            return Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(key))
                    .parseClaimsJws(token).getBody();
        } catch (Exception ex) {
            return null;
        }
    }


    /*
     * 生成token
     *
     * @param userId
     * @return
     */
    public static String createToken(Long userId, String key, int expireMinutes) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        //生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(key);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //添加构成JWT的参数
        JwtBuilder builder = Jwts.builder()
                // .setHeaderParam(“type”, “JWT”)
                .setSubject(userId.toString())
                .claim("userId", userId) // 设置载荷信息
                .setExpiration(DateTime.now().plusMinutes(expireMinutes).toDate())// 设置超时时间
                .signWith(signatureAlgorithm, signingKey);
        //生成JWT
        return builder.compact();
    }
    public static void main(String[] args) {
        String token = JWTUtil.createToken(1L,"admin", 30);
        System.out.println(token);
        Claims claims = JWTUtil.parseToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAwMDEiLCJ1c2VySWQiOjEwMDAwMSwiZXhwIjoxNjg0Mjk3MzE2fQ.W80-2jj2dwxq9LU1IxFTh--qGv255jaU-nnn4Vu958w", "USER");
        System.out.println(claims);
    }
}
