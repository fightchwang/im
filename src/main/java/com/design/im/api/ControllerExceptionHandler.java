package com.design.im.api;

import com.alibaba.fastjson.JSONObject;
import com.design.im.model.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public void handleServiceException(Exception e, HttpServletRequest request,
                                       HttpServletResponse response) {
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(response.getStatus());
        commonResponse.setMsg(e.getMessage());
        responseResult(response, commonResponse);
    }

    private void responseResult(HttpServletResponse response, CommonResponse commonResponse) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        try {
            response.getWriter().write(JSONObject.toJSONString(commonResponse));
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

}
