package com.activiti.bpmn.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDTO {

    // 用户名称
    private String userName;

    // 用户角色列表
    private List<String> roles;


}
