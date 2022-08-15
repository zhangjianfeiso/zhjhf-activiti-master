package com.activiti.bpmn.service.impl;

import com.activiti.bpmn.manager.ProcessImageManager;
import com.activiti.bpmn.service.ProcessImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.InputStream;

/**
 * 流程图service实现类
 *
 * @author Tethamo_zzx
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessImageServiceImpl implements ProcessImageService {

    ProcessImageManager processImageManager;

    @Autowired
    public ProcessImageServiceImpl(ProcessImageManager processImageManager) {
        this.processImageManager = processImageManager;
    }

    /**
     * 根据流程实例Id获取流程图
     *
     * @param procInstId 流程实例id
     * @return inputStream
     * @throws Exception exception
     */
    @Override
    public InputStream getFlowImgByProcInstId(String procInstId) throws Exception {
        return processImageManager.getFlowImgByProcInstId(procInstId);
    }

    @Override
    public InputStream getImgByDeploymentId(String deploymentId) throws Exception {
        return processImageManager.getImgByDeploymentId(deploymentId);
    }
}
