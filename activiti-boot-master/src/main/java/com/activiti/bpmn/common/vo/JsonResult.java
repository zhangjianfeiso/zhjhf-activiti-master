package com.activiti.bpmn.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description:
 * @author: fengli
 * @date: 2020/7/24 11:39 下午
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class JsonResult <T> implements Serializable {

    private int code;

    private String message;

    private T data;
}
