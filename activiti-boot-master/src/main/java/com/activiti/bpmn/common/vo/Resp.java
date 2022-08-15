package com.activiti.bpmn.common.vo;

import com.activiti.bpmn.common.enums.ResultEnum;

/**
 * @Description:
 * @author: fengli
 * @date: 2020/7/24 11:34 下午
 */
public class Resp {
    public static JsonResult ok() {
        return ok(ResultEnum.OK.getDesc());
    }

    public static JsonResult ok(String message) {
        return ok(null, message);
    }

    public static <T> JsonResult ok(T t) {
        return ok(t, ResultEnum.OK.getDesc());
    }

    public static <T> JsonResult ok(T data, String message) {
        return makeModelMap(ResultEnum.OK.getCode(), message, data);
    }

    public static JsonResult error() {
        return error(ResultEnum.INTERNAL_ERROR.getDesc());
    }

    public static JsonResult error(String message) {
        return error(null, message);
    }

    public static <T> JsonResult error(T data, String message) {
        return makeModelMap(ResultEnum.INTERNAL_ERROR.getCode(), message, data);
    }

    public static <T> JsonResult error(Integer code, String message, T data) {
        return makeModelMap(code, message, data);
    }

    private static JsonResult makeModelMap(Integer code, String message, Object data) {
        return JsonResult.builder().code(code).message(message).data(data).build();
    }
}
