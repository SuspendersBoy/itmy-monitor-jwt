package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.ClientRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("client")
public class RegisterClientController {
    @Autowired
    ClientRegisterService clientRegisterService;
    /*
    注册客户端(虚拟机)
     */
    @GetMapping("addClient")
    public RestBean<Void> addClientToRegister(@RequestHeader(value = "Authorization",required = false) String token) {
        if (token == null) return RestBean.unauthorized("请求参数为空");
        return  clientRegisterService.addClientToRegister(token) ? RestBean.success() : RestBean.failure(401,"注册失败,请检查token");
    }

}
