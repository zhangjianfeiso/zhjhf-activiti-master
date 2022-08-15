package com.activiti.bpmn.controller;

import cn.hutool.core.util.StrUtil;
import com.activiti.bpmn.common.exceptions.ActivitiCommonException;
import com.activiti.bpmn.common.exceptions.CustomException;
import com.activiti.bpmn.common.exceptions.ReturnMessageAndTemplateDef;
import com.activiti.bpmn.common.vo.JsonResult;
import com.activiti.bpmn.common.vo.Resp;
import com.activiti.bpmn.dto.ProcessInstanceDTO;
import com.activiti.bpmn.dto.UserDTO;
import com.activiti.bpmn.service.ProcessImageService;
import com.activiti.bpmn.service.ProcessInstanceService;
import com.activiti.bpmn.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Optional;

/**
 * @author zhjf
 * @version 1.0.0
 * @ClassName TaskController.java
 * @Description TODO
 * @createTime 2022年08月10日 17:55:00
 */
@Slf4j
@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

    private final ProcessInstanceService processInstanceService;
    private final ProcessImageService processImageService;
    private final TaskService taskService;

    /**
     * @title   流程定义（开启流程）
     * @description
     * @author zhjf
     * @updateTime 2022/8/10 16:18
     * @throws
     */
    @PostMapping("/start")
    public JsonResult createProcessInstance(@Validated ProcessInstanceDTO dto){
        Optional<ProcessInstance> optional = Optional.ofNullable(processInstanceService.createProcessInstance(dto.getProcessKey(),dto.getBusinessKey(),dto.getVariables()));
        if(optional.isPresent()){
            return Resp.ok("请求成功!",optional.map(ProcessInstance::getProcessInstanceId).orElseThrow(() -> new CustomException("开启流程失败!")));
        }
        return Resp.error("请求失败!");
    }

    /**
     * @title 获取任务列表
     * @description
     * @author zhjf
     * @updateTime 2022/8/10 18:03
     * @throws
     */
    @GetMapping("/getTaskList")
    public JsonResult getTaskList(){
        Collection<Task> tasks = taskService.listTasksByUser(new UserDTO());
        return Resp.ok(tasks);
    }


    /**
     * 生成流程图（完成任务高亮）
     * @param processInstanceId 流程实例ID
     * @param httpServletResponse response entity
     */
    @GetMapping("/image/{processInstanceId}")
    public void processImageGet(@PathVariable String processInstanceId, HttpServletResponse httpServletResponse){
        if (StrUtil.isBlank(processInstanceId)) {
            throw new ActivitiCommonException(ReturnMessageAndTemplateDef.Errors.UNKNOWN_PROCESS_NAME);
        }
        try {
            InputStream input = processImageService.getFlowImgByProcInstId(processInstanceId);
            writeProcess(input,httpServletResponse);
        } catch (Exception e) {
            throw new ActivitiCommonException(ReturnMessageAndTemplateDef.Errors.IMAGE_CREATE_FAIL,e);
        }
    }

    private void writeProcess(InputStream input,HttpServletResponse httpServletResponse)  throws Exception {
        byte[] bytes = IOUtils.toByteArray(input);
        httpServletResponse.setContentType("image/svg+xml");
        OutputStream outputStream = httpServletResponse.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }
}
