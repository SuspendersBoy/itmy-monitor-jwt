package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.BaseDetailDto;
import com.example.entity.vo.request.BaseDetailVO;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.service.ClientDetailService;
import com.example.service.ClientRegisterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("client")
public class RegisterClientController {
    @Autowired
    ClientRegisterService clientRegisterService;
    @Autowired
    ClientDetailService clientDetailService;

    /**
     * 注册服务器
     *
     * @param token
     * @return
     */
    @GetMapping("addClient")
    public RestBean<String> addClientToRegister(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null) return RestBean.unauthorized("请求参数为空");
        String id=clientRegisterService.addClientToRegister(token);
        return id!=null ? RestBean.success(id) : RestBean.failure(401, "注册失败,请检查token");
    }

    /**
     * 注册服务器配置信息
     * @param clientId
     * @param baseDetailVo
     * @return
     */
    @PostMapping("addClientDetail")
    public RestBean<Void> addClientDetail(
            @RequestHeader("ClientId")String clientId,
            @RequestBody BaseDetailVO baseDetailVo) {
        //封装dto信息
        BaseDetailDto baseDetailDto=new BaseDetailDto();
        BeanUtils.copyProperties(baseDetailVo,baseDetailDto);
        baseDetailDto.setClientId(clientId);
        clientDetailService.addClientDetail(baseDetailDto);
        return RestBean.success();
    }

    /**
     * 注册服务器实时信息
     * @param clientId 服务器id
     * @param runtimeDetailVO 服务器信息
     * @return
     */
    @PostMapping("runtime")
    public RestBean<Void> runtime(
            @RequestHeader("ClientId")String clientId ,
            @RequestBody RuntimeDetailVO runtimeDetailVO) {
        //封装dto信息
        clientDetailService.runtime(clientId,runtimeDetailVO);
            return RestBean.success();
    }
}
