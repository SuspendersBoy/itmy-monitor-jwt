package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.BaseDetailDto;
import com.example.entity.vo.request.RuntimeDetailVO;

public interface ClientDetailService extends IService<BaseDetailDto> {
    void addClientDetail(BaseDetailDto baseDetailDto);

    void addClientDetail(String clientId, RuntimeDetailVO runtimeDetailVO);
}
