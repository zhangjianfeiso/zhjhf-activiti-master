package com.activiti.bpmn.service.impl;

import com.activiti.bpmn.dto.UserDTO;
import com.activiti.bpmn.manager.TaskManager;
import com.activiti.bpmn.service.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collection;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskManager taskManager;


    @Override
    public Collection<Task> listTasksByUser(UserDTO userDTO) {
        return taskManager.listTasksByCandidateUser(userDTO);
    }
}
