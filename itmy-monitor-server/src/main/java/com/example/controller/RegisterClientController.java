package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.BaseDetailDto;
import com.example.entity.vo.request.BaseDetailVO;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.entity.vo.response.fluxClient;
import com.example.service.ClientDetailService;
import com.example.service.ClientRegisterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("client")
public class RegisterClientController {
    @Autowired
    ClientRegisterService clientRegisterService;
    @Autowired
    ClientDetailService clientDetailService;

    //注册服务器
    @GetMapping("addClient")
    public RestBean<String> addClientToRegister(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null) return RestBean.unauthorized("请求参数为空");
        String id = clientRegisterService.addClientToRegister(token);
        return id != null ? RestBean.success(id) : RestBean.failure(401, "注册失败,请检查token");
    }

    //注册服务器配置信息
    @PostMapping("addClientDetail")
    public RestBean<Void> addClientDetail(
            @RequestHeader("ClientId") String clientId,
            @RequestBody BaseDetailVO baseDetailVo) {
        //封装dto信息
        BaseDetailDto baseDetailDto = new BaseDetailDto();
        BeanUtils.copyProperties(baseDetailVo, baseDetailDto);
        baseDetailDto.setClientId(clientId);
        clientDetailService.addClientDetail(baseDetailDto);
        return RestBean.success();
    }

    //注册服务器实时信息
    @PostMapping("runtime")
    public RestBean<Void> runtime(
            @RequestHeader("ClientId") String clientId,
            @RequestBody RuntimeDetailVO runtimeDetailVO) {
        //封装dto信息
        clientDetailService.runtime(clientId, runtimeDetailVO);
        return RestBean.success();
    }

    //查询运行时数据
    @GetMapping("getRuntimeById")
    public RestBean<RuntimeDetailVO> getRuntimeById(String id) {
        return clientDetailService.getRuntimeById(id);
    }

    //查询运行时 历史数据
    @GetMapping("flux")
    public RestBean<List<fluxClient>> flux(String id) {
        return clientDetailService.flux(id);
    }

    //获取注册服务器 的token
    @GetMapping("getToken")
    public RestBean<String> getToken() {
        return clientRegisterService.getToken();
    }

    //删除客户端
    @DeleteMapping("deleteClient")
    public RestBean deleteClient(String id) {
        if (id == null) {
            return RestBean.forbidden("删除失败请检查");
        }
        clientDetailService.deleteClient(id);
        return RestBean.success();
    }
}
