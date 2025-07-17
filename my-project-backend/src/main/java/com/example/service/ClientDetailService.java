package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.client_register_dto.BaseDetailDto;

public interface ClientDetailService extends IService<BaseDetailDto> {
    void addClientDetail(BaseDetailDto baseDetailDto);
}
