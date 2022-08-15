package com.activiti;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.activiti.bpmn"})
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class, SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
public class ActivitiBootMasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActivitiBootMasterApplication.class, args);
    }

}
