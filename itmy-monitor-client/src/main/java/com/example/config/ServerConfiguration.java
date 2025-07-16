package com.example.config;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.ConnectionConfig;
import com.example.entity.Response;
import com.example.entity.netUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Slf4j
@Configuration
public class ServerConfiguration {
    /*
    连接操作初始化
     */
    @PostConstruct
    public void getConnectionConfig() throws IOException, InterruptedException {
        log.info("加载服务端连接配置....");
        //获取配置文件
        ConnectionConfig connectionConfig = getLocalConnectionConfig();
        if (connectionConfig == null) {
            log.error("配置文件为空");
            //由于是控制台输入,默认请求成功!不成功会一直循环控制台输入
            connectionConfig=getConnectionInformation(null);
            //配置文件为空,将用户输入正确的配置信息,存储的文件
            File file = new File("config/server.json");
            file.getParentFile().mkdirs();
            try (FileWriter fileWriter=new FileWriter(file)){
                fileWriter.write(JSONObject.toJSONString(connectionConfig));
            }
        }
        //配置文件不为空
        getConnectionInformation(connectionConfig);
    }

    /*
    获取控制台或配置类 发送连接 并封装返回信息
     */
    public ConnectionConfig  getConnectionInformation(ConnectionConfig connectionConfig) throws IOException, InterruptedException {
        log.info("正在发起连接");
        Response response;
        Map<String, String> map = new HashMap<>();
        if (connectionConfig == null) {  //判断配置文件是否为空
            //为空,控制台 输入请求信息
            Scanner sc = new Scanner(System.in);
            String address;
            String Token;
            do {
                log.info("请输入客户端地址");
                address = sc.nextLine();
                log.info("请输入客户端生成的token");
                Token = sc.nextLine();
                map.put("Authorization", Token);
                //netUtils.get此方法用于发送 请求(默认get)
                response = netUtils.get(address, null, map);
            } while (response.code() < 200 || response.code() > 300); //判断请求是否成功
            return new ConnectionConfig(address,Token);
        }
        //不为空,配置文件发送请求(默认get)
        map.put("Authorization", connectionConfig.getToken());
        response=netUtils.get(connectionConfig.getAddress(), null, map);
        if(response.code()<200 || response.code()>300) {  //判断请求是否成功,
            log.info("请检查配置文件正确");
        }
        return null;
    }

    /*
    获取本地的连接配置
     */
    public ConnectionConfig getLocalConnectionConfig() {
        File config = new File("config/server.json");
        if (config.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(config)) {
                String fig = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);
                return JSONObject.parseObject(fig, ConnectionConfig.class);
            } catch (Exception e) {
                log.error("读取配置文件失败");
                return null;
            }
        }
        return null;
    }
}
