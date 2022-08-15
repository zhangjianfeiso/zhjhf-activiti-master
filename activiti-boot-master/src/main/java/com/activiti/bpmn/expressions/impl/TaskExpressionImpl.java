package com.activiti.bpmn.expressions.impl;

import com.activiti.bpmn.expressions.TaskExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author zhjf
 * @version 1.0.0
 * @ClassName TaskExpressionImpl.java
 * @Description TODO
 * @createTime 2022年08月08日 14:33:00
 */
@Slf4j
@Service("taskExpression")
public class TaskExpressionImpl implements TaskExpression {

    @Override
    public boolean expression(String taskId2) {
        log.info("task__{}", taskId2);
        return true;
    }

    @Override
    public boolean expression2(String taskId2) {
        log.info("task__{}", taskId2);
        return false;
    }
}
