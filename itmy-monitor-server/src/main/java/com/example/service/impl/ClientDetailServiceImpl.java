package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.entity.dto.BaseDetailDto;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.entity.vo.response.ClientPreviewVO;
import com.example.mapper.BaseDetailMapper;
import com.example.service.ClientDetailService;
import com.example.utils.InfluxDbUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientDetailServiceImpl extends ServiceImpl<BaseDetailMapper, BaseDetailDto> implements ClientDetailService {
    ConcurrentHashMap<String,RuntimeDetailVO> runtimeData=new ConcurrentHashMap<>(); //服务器实时数据
    List<BaseDetailDto> clientDetails=new ArrayList<>();//服务器配置信息
    @Autowired
    InfluxDbUtils influxDbUtils;

    /**
     * 添加服务器配置信息
     * @param baseDetailDto 服务器配置信息
     */
    @Override
    public void addClientDetail(BaseDetailDto baseDetailDto) {
        clientDetails.add(baseDetailDto);
        this.save(baseDetailDto);
    }

    /**
     * 根据id添加服务器实时数据
     * @param clientId 服务器id
     * @param runtimeDetailVO 服务器实时数据
     */
    @Override
    public void runtime(String clientId, RuntimeDetailVO runtimeDetailVO) {
        runtimeData.put(clientId, runtimeDetailVO);
        influxDbUtils.writRuntimeData(clientId,runtimeDetailVO);

    }

    /**
     * 返回服务器所有信息
     * @return 服务器所有信息集合
     */
    @Override
    public List<ClientPreviewVO> listAllClient() {
        List<ClientPreviewVO>  vos=clientDetails.stream().map(baseDetailDto -> {
                    ClientPreviewVO vo = new ClientPreviewVO();
                    //将服务器信息拷贝至 ClientPreviewVO
                    BeanUtils.copyProperties(baseDetailDto, vo);
                    //获得服务器实时数据
                    RuntimeDetailVO runtime = runtimeData.get(baseDetailDto.getClientId());
                    //将服务器实时信息拷贝至 ClientPreviewVO
                     BeanUtils.copyProperties(runtime, vo);
                    //获得存放时间戳
                    long time = runtime.getTimestamp();
                    if (System.currentTimeMillis() - time < 45000) {
                        vo.setOnline(true);
                    }else {
                        vo.setOnline(false);
                    }

                    return vo;
                }
        ).toList();
        //遍历所有注册的服务器
        return  vos;
    }

}

