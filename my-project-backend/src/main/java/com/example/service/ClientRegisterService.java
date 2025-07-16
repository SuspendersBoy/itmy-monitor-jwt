package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.client_register_dto.ClientRegister;

public interface ClientRegisterService extends IService<ClientRegister>{
    Boolean addClientToRegister(String token);
    ClientRegister getClientRegisterById(Integer id);
    ClientRegister getClientRegisterByToken(String token);
}
