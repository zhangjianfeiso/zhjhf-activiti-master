package com.activiti.bpmn.dto;

import lombok.Data;
import java.util.Map;

/**
 * 描述：流程定义DTO
 */
@Data
public class ProcessInstanceDTO {


    private String processKey;
    private String businessKey;
    private Map<String,Object> variables;

}
