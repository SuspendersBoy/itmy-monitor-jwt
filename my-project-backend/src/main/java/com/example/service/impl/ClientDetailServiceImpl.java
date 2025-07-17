package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.client_register_dto.BaseDetailDto;
import com.example.mapper.BaseDetailMapper;
import com.example.service.ClientDetailService;
import org.springframework.stereotype.Service;

@Service
public class ClientDetailServiceImpl extends ServiceImpl<BaseDetailMapper, BaseDetailDto> implements ClientDetailService {
    @Override
    public void addClientDetail(BaseDetailDto baseDetailDto) {
        this.save(baseDetailDto);
    }
}
