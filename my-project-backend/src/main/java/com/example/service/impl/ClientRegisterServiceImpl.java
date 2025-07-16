package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.client_register_dto.ClientRegister;
import com.example.mapper.ClientRegisterMapper;
import com.example.service.ClientRegisterService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClientRegisterServiceImpl extends ServiceImpl<ClientRegisterMapper, ClientRegister> implements ClientRegisterService {
    //生成token
    private String random = generateNewToken();
    //根据id 存储所有的虚拟机信息
    private Map<Integer, ClientRegister> idClient = new ConcurrentHashMap<>();
    //根据token 存储所有的虚拟机信息
    private Map<String, ClientRegister> tokenClient = new ConcurrentHashMap<>();

    /**
     * 查询数据库信息,初始化 clientMap
     */
    @PostConstruct
    public void init() {
        System.out.println("random密钥:==============="+random);
        this.list().forEach(this::addClientRegister);
    }

    /**
     * 添加客户端(服务器) 到客户端注册表
     *
     * @param token
     * @return 成功/失败
     */
    @Override
    public Boolean addClientToRegister(String token) {
        if (this.random.equals(token)) {
            //封装信息
            ClientRegister clientRegister = new ClientRegister();
            clientRegister.setName("未命名");
            clientRegister.setToken(token);
            clientRegister.setRegisterTime(new Date());
            //插入数据
            if (this.save(clientRegister)) {
                //更新 map 存储的客户端(虚拟机)信息
                this.addClientRegister(clientRegister);
                return true;
            }
        }
        return false;
    }

    /**
     * 通过id获取ClientRegister form idClient
     *
     * @param id 客户端(虚拟机)id
     * @return 客户端(虚拟机) 信息
     */
    @Override
    public ClientRegister getClientRegisterById(Integer id) {
        return idClient.get(id);
    }

    /**
     * 通过token获取ClientRegister form idClient
     *
     * @param token 客户端(虚拟机) token
     * @return 客户端(虚拟机) 信息
     */
    @Override
    public ClientRegister getClientRegisterByToken(String token) {
        return tokenClient.get(token);
    }

    /**
     * 用于更新 map 存储的客户端(虚拟机)信息
     *
     * @param clientRegister 需要注册客户端(虚拟机)的信息
     */
    public void addClientRegister(ClientRegister clientRegister) {
        idClient.put(clientRegister.getId(), clientRegister);
        tokenClient.put(clientRegister.getToken(), clientRegister);
    }

    /**
     * 生成token
     *
     * @return 返回一个随机生成的 token
     */
    private String generateNewToken() {
        String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(24);
        for (int i = 0; i < 24; i++)
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        return sb.toString();
    }
}
