package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.ClientRegisterDto;

public interface ClientRegisterService extends IService<ClientRegisterDto>{
    String addClientToRegister(String token);
    ClientRegisterDto getClientRegisterById(Integer id);
    ClientRegisterDto getClientRegisterByToken(String token);
}
