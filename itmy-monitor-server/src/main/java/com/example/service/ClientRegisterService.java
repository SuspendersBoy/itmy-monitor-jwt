package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.BaseDetailDto;
import com.example.entity.dto.ClientRegisterDto;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.entity.vo.response.ClientPreviewVO;

import java.util.List;

public interface ClientRegisterService extends IService<ClientRegisterDto>{
    String addClientToRegister(String token);
    ClientRegisterDto getClientRegisterById(Integer id);
    ClientRegisterDto getClientRegisterByToken(String token);

}
