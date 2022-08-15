package com.activiti.bpmn.dto;

import lombok.Data;

/**
 * @author zhjf
 * @version 1.0.0
 * @ClassName BasePageDto.java
 * @Description TODO
 * @createTime 2022年08月11日 11:11:00
 */
@Data
public class BasePageDto {

    private int page = 1;
    private int limit = 10;
}
