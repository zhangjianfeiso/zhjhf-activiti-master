package com.activiti.bpmn.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * @author zhjf
 * @version 1.0.0
 * @ClassName ModelDto.java
 * @Description TODO
 * @createTime 2022年08月10日 14:29:00
 */
@Data
public class ModelDTO {


    private String id;

    private String category;
    /**
     * 流程定义key
     */
    @NotBlank(message = "流程定义key不能为空!")
    private String processKey;
    /**
     * 流程定义名字
     */
    @NotBlank(message = "流程定义名字不能为空!")
    private String processName;
    /**
     * 流程定义内容
     */
    @NotBlank(message = "流程定义内容Xml不能为空!")
    private String processXml;

    @NotBlank(message = "流程定义内容SVG不能为空!")
    private String processSvg;

}
