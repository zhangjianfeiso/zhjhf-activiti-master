package com.activiti.bpmn.dto;

import lombok.Data;
import java.util.Map;

/**
 * @author zhjf
 * @version 1.0.0
 * @ClassName TaskDTO.java
 * @Description TODO
 * @createTime 2022年08月10日 16:56:00
 */
@Data
public class TaskDTO {

    private String processInstanceId;
    private Map<String,Object> variables;

}
