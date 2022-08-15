package com.activiti.bpmn.controller;

import cn.hutool.core.util.StrUtil;
import com.activiti.bpmn.common.exceptions.ActivitiCommonException;
import com.activiti.bpmn.common.exceptions.ReturnMessageAndTemplateDef;
import com.activiti.bpmn.common.vo.JsonResult;
import com.activiti.bpmn.common.vo.Resp;
import com.activiti.bpmn.dto.ModelDTO;
import com.activiti.bpmn.dto.ModelPageDto;
import com.activiti.bpmn.service.ProcessImageService;
import com.activiti.bpmn.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.repository.Model;
import org.apache.commons.io.IOUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 功能描述:部署
 */
@Slf4j
@RestController
@RequestMapping("/process")
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessService processService;
    private final ProcessImageService processImageService;

    /**
     * @title 保存模型
     * @description
     * @author zhjf
     * @updateTime 2022/8/10 16:00
     * @throws
     */
    @PostMapping("/saveModel")
    public JsonResult saveModel(@Validated @RequestBody ModelDTO dto)throws Exception{
        Model model = processService.saveModel(dto);
        return Resp.ok(model.getId(),"保存成功!");
    }

    /**
     * @title 获取模型列表
     * @description
     * @author zhjf
     * @updateTime 2022/8/11 14:48
     * @throws
     */
    @PostMapping("/modelPage")
    public JsonResult modelPage(@RequestBody @Validated ModelPageDto dto){
        return Resp.ok(processService.getModelPage(dto));
    }

    /**
     * @title 删除模型
     * @description
     * @author zhjf
     * @updateTime 2022/8/10 18:00
     * @throws
     */
    @GetMapping("/deleteModel/{modelid}")
    public JsonResult deleteModel(@PathVariable("modelId") String modelId){
        processService.deleteModel(modelId);
        return Resp.ok("删除成功!");
    }

    /**
     * @title 获取流程图XML
     * @description
     * @author zhjf
     * @updateTime 2022/8/10 16:15
     * @throws
     */
    @GetMapping(value = "/getProcessXml/{modelId}")
    public JsonResult getProcessXml(@PathVariable("modelId") String modelId){
        return Resp.ok(processService.getProcessXml(modelId),"获取ProcessXml成功!");
    }

    /**
     * @title 获取部署流程图
     * @description
     * @author zhjf
     * @updateTime 2022/8/10 17:53
     * @throws
     */
    @GetMapping("/deployment/image/{deploymentId}")
    public void processImageDeploymentGet(@PathVariable String deploymentId, HttpServletResponse httpServletResponse){
        if (StrUtil.isBlank(deploymentId)) {
            throw new ActivitiCommonException(ReturnMessageAndTemplateDef.Errors.UNKNOWN_PROCESS_NAME);
        }
        try {
            InputStream input = processImageService.getImgByDeploymentId(deploymentId);
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

    /**
     * 流程部署
     * @return 返回成功与否
     */
    @RequestMapping("/deploy/{modelId}")
    public JsonResult deploy(@PathVariable("modelId") String modelId) {
        if(StrUtil.isNotBlank(processService.deploy(modelId))){
            return Resp.ok("部署成功!");
        }
        return Resp.error("部署失败!");
    }

}
