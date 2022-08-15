package com.activiti.bpmn.common.exceptions;

import com.activiti.bpmn.common.vo.JsonResult;
import com.activiti.bpmn.common.vo.Resp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhjf
 * @version 1.0.0
 * @ClassName GlobalControllerAdvice.java
 * @Description TODO
 * @createTime 2022年08月11日 10:59:00
 */
@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {
    private static final String BAD_REQUEST_MSG = "客户端请求参数错误";

    // <1> 处理 form data方式调用接口校验失败抛出的异常
    @ExceptionHandler(BindException.class)
    public JsonResult bindExceptionHandler(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> collect = fieldErrors.stream()
                .map(o -> o.getDefaultMessage())
                .collect(Collectors.toList());
        return Resp.error(HttpStatus.BAD_REQUEST.value(), collect.stream().findFirst().get(), collect);
    }

    // <2> 处理 json 请求体调用接口校验失败抛出的异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public JsonResult methodArgumentNotValidExceptionHandler(HttpServletResponse httpServletResponse, MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> collect = fieldErrors.stream()
                .map(o -> o.getDefaultMessage())
                .collect(Collectors.toList());
        return Resp.error(HttpStatus.BAD_REQUEST.value(), collect.stream().findFirst().get(), collect);
    }

    // <3> 处理单个参数校验失败抛出的异常
    @ExceptionHandler(ConstraintViolationException.class)
    public JsonResult constraintViolationExceptionHandler(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> collect = constraintViolations.stream()
                .map(o -> o.getMessage())
                .collect(Collectors.toList());
        return Resp.error(HttpStatus.BAD_REQUEST.value(), collect.stream().findFirst().get(), collect);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonResult handle(Exception e) {
        log.error(e.getMessage(), e);
        return Resp.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage(),null);
    }
}


