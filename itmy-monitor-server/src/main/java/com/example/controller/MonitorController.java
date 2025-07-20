package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.response.ClientPreviewVO;
import com.example.service.ClientDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("api/monitor")
public class MonitorController {
    @Autowired
    ClientDetailService clientDetailService;
    @GetMapping("list")
    public RestBean<List<ClientPreviewVO>> listAllClient(){
       return RestBean.success(clientDetailService.listAllClient());
    }
}
