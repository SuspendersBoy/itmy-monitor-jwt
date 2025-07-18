package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.BaseDetailDto;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.mapper.BaseDetailMapper;
import com.example.service.ClientDetailService;
import com.example.utils.InfluxDbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientDetailServiceImpl extends ServiceImpl<BaseDetailMapper, BaseDetailDto> implements ClientDetailService {
   @Autowired
    InfluxDbUtils influxDbUtils;
    @Override
    public void addClientDetail(BaseDetailDto baseDetailDto) {
        this.save(baseDetailDto);
    }

    ConcurrentHashMap<String,RuntimeDetailVO> runtimeData=new ConcurrentHashMap();
    @Override
    public void addClientDetail(String clientId, RuntimeDetailVO runtimeDetailVO) {
        runtimeData.put(clientId, runtimeDetailVO);
        influxDbUtils.writRuntimeData(clientId,runtimeDetailVO);
    }
}
