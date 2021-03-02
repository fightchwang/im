package com.design.im.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.util.Calendar;
import java.util.Date;

import static com.design.im.util.Constants.TOKEN_EXPIRE;

public class JwtUtils {

    /**
     签发对象：这个用户的id
     签发时间：现在
     有效时间：60分钟
     载荷内容：username
     加密密钥：这个人的id加上一串字符串
     */
    public static String createToken(String userId, String userName, int expireInMinutes) {

        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, expireInMinutes <= TOKEN_EXPIRE ? TOKEN_EXPIRE : expireInMinutes);
        Date expiresDate = nowTime.getTime();

        return JWT.create().withAudience(userId)   //签发对象
                .withIssuedAt(new Date())    //发行时间
                .withExpiresAt(expiresDate)  //有效时间
                .withClaim("userName", userName)    //载荷，随便写几个都可以
                .sign(Algorithm.HMAC256(userId));   //加密
    }

    /**
     * 检验合法性，其中secret参数就应该传入的是用户的id
     * @param token
     * @return true if the token is valid
     */
    public static boolean verifyToken(String token, String secret) throws RuntimeException {
        boolean pass = false;
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            jwt = verifier.verify(token);
            pass = true;
        } catch (Exception e) {
            //效验失败
        }
        return pass;
    }

    /**
     * 获取签发对象
     */
    public static String getAudience(String token) throws RuntimeException {
        String audience = null;
        try {
            audience = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            //这里是token解析失败
            throw new RuntimeException();
        }
        return audience;
    }


    /**
     * 通过载荷名字获取载荷的值
     */
    public static Claim getClaimByName(String token, String name){
        return JWT.decode(token).getClaim(name);
    }

    /**
     * 判断token是否过期
     * true 如果过期时间小于当前时间
     */
    public static boolean isTokenExpired(String token, long now){
        Claim exp = getClaimByName(token, "exp");
        return exp.asDate().getTime() < now;
    }
}
