package com.activiti.bpmn.service;

import com.activiti.bpmn.dto.UserDTO;
import org.activiti.engine.task.Task;

import java.util.Collection;

public interface TaskService {

    Collection<Task> listTasksByUser(UserDTO userDTO);
}
