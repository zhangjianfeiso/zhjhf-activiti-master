package com.activiti.bpmn.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.activiti.bpmn.common.utils.VerificationUtils;
import com.activiti.bpmn.dto.ModelDTO;
import com.activiti.bpmn.common.utils.function.FunctionUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.xpath.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * functions:
 * ①挂起流程定义（普通、级联）
 * ②激活流程定义（普通、级联）
 * ③查询流程定义状态（单个、所有）
 * ④检查单个流程定义是否存在
 *
 * @author Tethamo_zzx
 */
@Component
@Slf4j
public class ProcessManager {

    RepositoryService repositoryService;


    @Autowired
    public ProcessManager(RepositoryService repositoryService,
                          RuntimeService runtimeService,
                          TaskService taskService) {
        this.repositoryService = repositoryService;
    }

    /**
     * 将该流程定义挂起，则之后创建流程实例时会失败
     *
     * @param processDefinitionName 流程定义name
     */
    @Transactional(rollbackFor = Exception.class)
    public void suspendProcessDefinitionByName(String processDefinitionName) {
        ProcessDefinition processDefinition = VerificationUtils.checkProcessDefinitionByName(processDefinitionName);
        repositoryService.suspendProcessDefinitionById(processDefinition.getId());
    }

    /**
     * 将该流程定义挂起，则之后创建流程实例时会失败
     * 若是级联挂起，则之前创建的流程实例也会被挂起
     * 同时可以自定义什么时间开始挂起这个流程定义的时间，若null则立即挂起
     *
     * @param processDefinitionName 流程定义name
     * @param cascade               是否级联挂起
     * @param suspensionDate        多久之后开始挂起
     */
    @Transactional(rollbackFor = Exception.class)
    public void cascadeSuspendProcessDefinitionByName(String processDefinitionName, boolean cascade, Date suspensionDate) {
        ProcessDefinition processDefinition = VerificationUtils.checkProcessDefinitionByName(processDefinitionName);
        repositoryService.suspendProcessDefinitionById(processDefinition.getId(), cascade, suspensionDate);
    }


    /**
     * 将该流程定义激活，则之后创建流程实例时会成功
     *
     * @param processDefinitionName 流程定义name
     */
    @Transactional(rollbackFor = Exception.class)
    public void activateProcessDefinitionByName(String processDefinitionName) {
        ProcessDefinition processDefinition = VerificationUtils.checkProcessDefinitionByName(processDefinitionName);
        repositoryService.activateProcessDefinitionById(processDefinition.getId());
    }

    /**
     * 将该流程定义激活，则之后创建流程实例时会成功
     * 若是级联激活，则有关该流程定义的被挂起的流程实例也会被激活
     * 同时可以自定义什么时间开始激活这个流程定义的时间，若null则立即激活
     *
     * @param processDefinitionName 流程定义name
     * @param cascade               是否级联激活
     * @param activationDate        多久之后开始激活
     */
    @Transactional(rollbackFor = Exception.class)
    public void cascadeActivateProcessDefinitionByName(String processDefinitionName, boolean cascade, Date activationDate) {
        ProcessDefinition processDefinition = VerificationUtils.checkProcessDefinitionByName(processDefinitionName);
        repositoryService.activateProcessDefinitionById(processDefinition.getId(), cascade, activationDate);
    }

    /**
     * 通过流程定义name获取流程定义状态
     *
     * @param processDefinitionName 流程定义name
     * @return ProcessDefinition 流程定义
     */
    public Boolean getProcessDefinitionStatusByName(String processDefinitionName) {
        // 先检查是否存在，不需要获取返回值
        VerificationUtils.checkProcessDefinitionByName(processDefinitionName);
        // 获取激活装态下的它，如果处于激活能获取到，如果处于挂起则获取不到，能否获取到直接返回true、false
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionName(processDefinitionName)
                .latestVersion()
                .active()
                .singleResult();
        return Objects.nonNull(processDefinition);
    }

    /**
     * @return 返回所有流程定义的状态（挂起或激活）
     */
    public Map<String, Boolean> getProcessConfigStatusMap() {
        Map<String, Boolean> map = new HashMap<>();
        // 放入所有的激活状态的流程定义
        repositoryService.createProcessDefinitionQuery()
                .active()
                .latestVersion()
                .list()
                .forEach(processDefinition -> map.put(processDefinition.getName(), true));
        // 放入所有的挂起状态的流程定义
        repositoryService.createProcessDefinitionQuery()
                .suspended()
                .latestVersion()
                .list()
                .forEach(processDefinition -> map.put(processDefinition.getName(), false));
        return map;
    }


    /**
     * @param processDefinitionName 流程定义name
     * @return ProcessDefinition
     */
    public ProcessDefinition getProcessDefinitionByName(String processDefinitionName) {
        return toProcessDefinitionDTO(VerificationUtils.checkProcessDefinitionByName(processDefinitionName));
    }

    /**
     * 获取所有的流程定义DTO
     *
     * @return 一个不可修改的流程定义列表
     */
    public Collection<ProcessDefinition> getProcessDefinitions() {
        return Collections.unmodifiableList(repositoryService.createProcessDefinitionQuery()
                        .latestVersion()
                        .list())
                .stream()
                .map(this::toProcessDefinitionDTO)
                .collect(Collectors.toList());
    }

    /**
     * 普通删除，如果当前规则下有正在执行的流程，则抛异常
     *
     * @param processDefinitionName 流程定义name
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteProcessDefinitionByName(String processDefinitionName) {
        ProcessDefinition processDefinition = VerificationUtils.checkProcessDefinitionByName(processDefinitionName);
        repositoryService.deleteDeployment(processDefinition.getDeploymentId());
    }

    /**
     * 级联删除,会删除和当前规则相关的所有信息，包括该流程定义下的所有的流程实例的历史数据
     *
     * @param processDefinitionName 流程定义name
     */
    @Transactional(rollbackFor = Exception.class)
    public void cascadeDeleteProcessDefinitionByName(String processDefinitionName) {
        ProcessDefinition processDefinition = VerificationUtils.checkProcessDefinitionByName(processDefinitionName);
        repositoryService.deleteDeployment(processDefinition.getDeploymentId(), true);
    }

    /**
     * ProcessDefinition转换为ProcessDefinition
     *
     * @param processDefinition 流程定义
     * @return ProcessDefinition
     */
    public ProcessDefinition toProcessDefinitionDTO(ProcessDefinition processDefinition) {
        return processDefinition;
//        return new ProcessDefinitionDTO(
//                processDefinition.getId(),
//                processDefinition.getName(),
//                !processDefinition.isSuspended());
    }

    public Model saveModel(ModelDTO dto) throws Exception{
        List<Model> modelList = repositoryService.createModelQuery().modelKey(dto.getProcessKey()).list();
        //判断processKey是否存在
        FunctionUtils.isWrong(CollectionUtil.isNotEmpty(modelList) && modelList.stream().anyMatch(m -> !m.getId().equals(dto.getId()))).throwCustomException("该流程定义key已存在!");
        Model model = StrUtil.isBlank(dto.getId())?repositoryService.newModel():repositoryService.getModel(dto.getId());
        FunctionUtils.isWrong(null == model).throwCustomException("未查询到模型!");
        JSONObject jsonObject = StrUtil.isNotBlank(model.getMetaInfo())?JSONObject.parseObject(model.getMetaInfo()):new JSONObject();
        jsonObject.put("processId",dto.getId());
        jsonObject.put("processName", dto.getProcessName());
        jsonObject.put("processKey", dto.getProcessKey());
        model.setMetaInfo(jsonObject.toJSONString());
        model.setName(dto.getProcessName());
        model.setKey(dto.getProcessKey());
        model.setCategory(dto.getCategory());
        model.setDeploymentId(null);
        repositoryService.saveModel(model);
        repositoryService.addModelEditorSource(model.getId(), dto.getProcessXml().getBytes("UTF-8"));
        InputStream svgStream = new ByteArrayInputStream(dto.getProcessSvg().getBytes("UTF-8"));
        TranscoderInput input = new TranscoderInput(svgStream);
        PNGTranscoder transcoder = new PNGTranscoder();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(outStream);
        transcoder.transcode(input, output);
        final byte[] result = outStream.toByteArray();
        repositoryService.addModelEditorSourceExtra(model.getId(), result);
        outStream.close();
        return model;
    }

    public void deleteModel(String modelId){
        repositoryService.deleteModel(modelId);
    }

    public Page<Model> getModelPage(String category, String processKey, String processName, int page, int limit){
        ModelQuery modelQuery = repositoryService.createModelQuery();
        if(StrUtil.isNotBlank(category)){
            modelQuery = modelQuery.modelCategory(category);
        }
        if(StrUtil.isNotBlank(processKey)){
            modelQuery = modelQuery.modelKey(processKey);
        }
        if(StrUtil.isNotBlank(processName)){
            modelQuery = modelQuery.modelNameLike("%" + processName + "%");
        }
        Page<Model> modelPage = new Page<>(page + 1, limit);
        List<Model> modelList = modelQuery.orderByCreateTime().desc().listPage(page, limit);
        modelPage.setTotal(modelQuery.count());
        modelPage.setRecords(modelList);
        return modelPage;
    }

    /**
     * @title 流程部署
     * @description
     * @author zhjf
     * @param modelId 模型id
     * @updateTime 2022/8/10 15:54
     * @throws
     */
    public String deploy(String modelId){
        Model model = repositoryService.getModel(modelId);
        byte[] bpmnXmlBytes = repositoryService.getModelEditorSource(modelId);
        byte[] bpmnSvgBytes = repositoryService.getModelEditorSourceExtra(modelId);
        try {
            // 创建部署对象并部署
            Deployment deploy = repositoryService.createDeployment()
                    .name(model.getName())
                    .key(model.getKey())
                    .addString(model.getKey() + ".bpmn20.xml", new String(bpmnXmlBytes, StandardCharsets.UTF_8))
                    .addBytes(model.getKey() + ".png",bpmnSvgBytes)
                    .deploy();
            log.info(deploy.getId());
            // 如果没有部署成功，processDefinition.getName()会报错
            model.setDeploymentId(deploy.getId());
            repositoryService.saveModel(model);
            return deploy.getId();
        } catch (Exception e) {
            log.info("fail");
            FunctionUtils.isWrong(true).throwCustomException("fail");
        }
        return null;
    }
    /**
     * @title 获取流程图XML
     * @description
     * @author zhjf
     * @updateTime 2022/8/10 16:09
     * @throws
     */
    public String getProcessXml(String modelId){
        byte[] bpmnXmlBytes = repositoryService.getModelEditorSource(modelId);
        return new String(bpmnXmlBytes,StandardCharsets.UTF_8);
    }



}
