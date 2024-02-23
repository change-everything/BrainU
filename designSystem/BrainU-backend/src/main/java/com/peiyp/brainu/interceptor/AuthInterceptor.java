package com.peiyp.brainu.interceptor;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSONObject;
import com.peiyp.brainu.entity.vo.R;
import com.peiyp.brainu.util.JWTUtil;
import io.jsonwebtoken.Claims;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author PeiYP
 * @since 2023年05月16日 19:59
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        if (requestURI.contains("/auth/login") || "openapi".contains(requestURI)) {
            return true;
        }
        String authorization = request.getHeader("Authorization");
        String token = authorization.replace("Bearer ", "");
        Claims user = JWTUtil.parseToken(token, "USER");
        if (user == null) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = null;
            try {
                R error = R.error(40001, "认证已过期");
                String json = JSONObject.toJSONString(error);
                response.setContentType("application/json");
                out = response.getWriter();
                // 返回json信息给前端
                out.print(json);
                out.flush();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(500);
                return false;
            }
        }
        String result = stringRedisTemplate.opsForValue().get("login:token:" + user.getSubject());
        if (result != null) {
            request.setAttribute("doctorId", user.getSubject());
            return true;
        } else {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = null;
            try {
                R error = R.error(40001, "认证已过期");
                String json = JSONObject.toJSONString(error);
                response.setContentType("application/json");
                out = response.getWriter();
                // 返回json信息给前端
                out.print(json);
                out.flush();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(500);
                return false;
            }
        }
    }


//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        String authorization = request.getHeader("Authorization");
//        String token = authorization.replace("Bearer ", "");
//        Claims user = JWTUtil.parseToken(token, "USER");
//        if (user != null) {
//            long exp = user.getExpiration().getTime();
//            long now = new Date().getTime();
//            long current = exp - now;
//            LocalDateTime localDateTime = LocalDateTimeUtil.ofUTC(current);
//            int minute = localDateTime.getMinute();
//            if (minute < 10) {
//                String newToken = JWTUtil.createToken(Long.valueOf(user.getSubject()), "USER", 30);
//                response.addHeader("newToken", newToken);
//            }
//        }
//    }
}
