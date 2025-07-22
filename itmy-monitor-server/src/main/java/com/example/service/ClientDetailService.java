package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.RestBean;
import com.example.entity.dto.BaseDetailDto;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.entity.vo.response.ClientPreviewVO;
import com.example.entity.vo.response.fluxClient;

import java.util.List;

public interface ClientDetailService extends IService<BaseDetailDto> {
    void addClientDetail(BaseDetailDto baseDetailDto);
    List<ClientPreviewVO> listAllClient();
    void runtime (String clientId, RuntimeDetailVO runtimeDetailVO);

    RestBean<RuntimeDetailVO> getRuntimeById(String id);

    RestBean<List<fluxClient>> flux(String id);
}
