package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.example.entity.RestBean;
import com.example.entity.dto.BaseDetailDto;
import com.example.entity.dto.ChildDto;
import com.example.entity.dto.ClientSshDTO;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.entity.vo.request.SshConnectionVO;
import com.example.entity.vo.response.ClientPreviewVO;
import com.example.entity.vo.response.fluxClient;
import com.example.mapper.AccountSubMapper;
import com.example.mapper.BaseDetailMapper;
import com.example.mapper.SshMapper;
import com.example.service.ClientDetailService;
import com.example.utils.InfluxDbUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientDetailServiceImpl extends ServiceImpl<BaseDetailMapper, BaseDetailDto> implements ClientDetailService {
    ConcurrentHashMap<String,RuntimeDetailVO> runtimeData=new ConcurrentHashMap<>(); //服务器实时数据
    List<BaseDetailDto> clientDetails=new ArrayList<>();//服务器配置信息
    @Autowired
    InfluxDbUtils influxDbUtils;
    @Autowired
    AccountSubMapper accountSubMapper;
    @Autowired
    SshMapper sshMapper;
    @Override
    /**
     * 根据服务器id删除 服务器
     */
    public void deleteClient(String id) {
        runtimeData.remove(id);
        Iterator<BaseDetailDto> iterator=clientDetails.iterator();
        while (iterator.hasNext()) {
            BaseDetailDto baseDetailDto=iterator.next();
            if(baseDetailDto.getClientId().equals(id)){
                clientDetails.remove(baseDetailDto);
            }
        }
    }

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
     * 查询 服务器 运行时数据
     * @param id 服务器id
     * @return 服务器运行数据
     */
    @Override
    public RestBean<RuntimeDetailVO> getRuntimeById(String id) {
        RuntimeDetailVO r=runtimeData.get(id);
        r.setClientId(id);
        return RestBean.success(r);
    }

    /**
     *查询服务器运行时历 史数据
     * @param id 服务器id
     * @return 服务器运行时 历史数据
     */
    @Override
    public RestBean<List<fluxClient>> flux(String id) {
        return RestBean.success(influxDbUtils.getRuntimeDataById(id));
    }

    /**
     * 返回服务器所有信息
     * @return 服务器所有信息集合
     */
    @Override
    public List<ClientPreviewVO> listAllClient() {
       try {
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
                       //封装 clientId
                       vo.setClientId(baseDetailDto.getClientId());
                       return vo;
                   }
           ).toList();
           //遍历所有注册的服务器
           return  vos;
       }catch (Exception e){
           return null;
       }
    }
    /**
     * 返回user用户分配服务器所有信息
     * @return 服务器所有信息集合
     */
    @Override
    public List<ClientPreviewVO> listAllUserClient(String id) {
        ChildDto childDto =accountSubMapper.selectById(id);
        List<ClientPreviewVO> vos=clientDetails.stream().map(baseDetailDto -> {
                    if(baseDetailDto.getClientId().equals(childDto.getClient())){
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
                        //封装 clientId
                        vo.setClientId(baseDetailDto.getClientId());
                        return vo;
                    }
                    return null;
                }
        ).toList();
        //除空
        List<ClientPreviewVO> list=new  ArrayList<>();
        vos.stream().forEach(vo -> {
            if(vo!=null){
                list.add(vo);
            }
        });
        return  list;
    }

    /**
     * 添加服务器连接信息
     * @param sshConnectionVO
     */
    @Override
    public void sshSave(SshConnectionVO sshConnectionVO) {
        ClientSshDTO clientSshDTO=new ClientSshDTO();
        BeanUtils.copyProperties(sshConnectionVO,clientSshDTO);
        sshMapper.insert(clientSshDTO);
    }

    @Override
    public boolean sshSettings(String clientId) {
        Map<String,Object> pram=new HashMap<>();
        pram.put("client_id",clientId);
        List<ClientSshDTO> clientShh=sshMapper.selectByMap(pram);
        return clientShh != null && clientShh.size() > 0;
    }


}

