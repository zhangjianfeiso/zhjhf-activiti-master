package com.activiti.bpmn.expressions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zhjf
 * @version 1.0.0
 * @ClassName TaskExpression.java
 * @Description TODO
 * @createTime 2022年08月08日 14:03:00
 */
public interface TaskExpression {

    public boolean expression(String taskId2);

    public boolean expression2(String taskId2);

}
