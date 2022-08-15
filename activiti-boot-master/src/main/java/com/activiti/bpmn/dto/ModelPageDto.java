package com.activiti.bpmn.dto;

import lombok.Data;

/**
 * @author zhjf
 * @version 1.0.0
 * @ClassName ModelPageDto.java
 * @Description TODO
 * @createTime 2022年08月11日 11:12:00
 */
@Data
public class ModelPageDto extends BasePageDto{

    private String category;
    /**
     * 流程定义key
     */
    private String processKey;
    /**
     * 流程定义名字
     */
    private String processName;
}
