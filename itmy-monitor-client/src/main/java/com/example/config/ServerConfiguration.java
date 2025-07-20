package com.example.config;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.BaseDetail;
import com.example.entity.ConnectionConfig;
import com.example.entity.Response;
import com.example.entity.RuntimeDetail;
import com.example.utils.netUtils;
import com.example.utils.oshiUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    oshiUtils oshiUtils;

    String id; //注册成功返回的主键id
    String address;; //封装请求地址
    Map<String, String> map = new HashMap<>();//用于封装请求头
    RuntimeDetail runtimeDetail=new RuntimeDetail();
    /**
     * 连接操作初始化
     * @throws IOException
     * @throws InterruptedException
     */
    @PostConstruct
    public void getConnectionConfig() throws InterruptedException {
        log.info("加载服务端文件配置....");
        //获取配置文件
        ConnectionConfig connectionConfig = getLocalConnectionConfig();
        if (connectionConfig == null) {
            log.error("读取配置文件失败");
            //由于是控制台输入,默认请求成功!不成功会一直循环控制台输入
            getConnectionInformation();
        }else {
            log.error("读取配置文件成功");
            //配置文件不为空
            getConnectionInformation(connectionConfig);
        }

        //上传配置信息
        detail();
        //上传运行时数据
        while (true) {
            runtime();
            Thread.sleep(10000);
        }
    }

    /**
     * 获取服务器运行时数据
     */
    public  void runtime()  {
        log.info("读取服务器运行时数据");
        runtimeDetail=oshiUtils.getRuntimeDetail();
        if (runtimeDetail != null) {
            map.put("ClientId",id);
            int last=address.lastIndexOf("/");
            address=address.substring(0,last+1)+"runtime";
            try {
                netUtils.postJson(address,JSONObject.toJSONString(runtimeDetail),map);
            }catch (Exception e){
                log.error("请检查请求地址");
            }
        }
    }

    /**
     * 获取服务器配置信息
     */
    public void  detail()  {
        BaseDetail baseDetail =oshiUtils.printHardwareInfo();
       if(baseDetail!=null){
           map.put("ClientId",id);
           int last=address.lastIndexOf("/");
           address=address.substring(0,last+1)+"addClientDetail";
           try {
               netUtils.postJson(address,JSONObject.toJSONString(baseDetail),map);
           } catch (Exception e){
               log.error("请检查请求地址");
           }
       }
    }

    /**
     * 通过配置文件 发送连接 并封装返回信息
     * @param connectionConfig 配置文件信息
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public void getConnectionInformation(ConnectionConfig connectionConfig) {
        Response response;
        map.put("Authorization", connectionConfig.getToken());
        address=connectionConfig.getAddress();
        log.info("正在发起连接");
        response=netUtils.get(address, null, map);
        if(response.code()<200 || response.code()>300) {  //判断请求是否成功,
            log.info("请检查配置文件是否正确");
            log.info("正在转向手动输入配置信息,请稍等.......");
            getConnectionInformation();
        }else {
            id=(String)response.data();
        }

    }

    /**
     * 手动输入配置信息
     * @return
     */
    public void   getConnectionInformation() {
        Response response;
        String Token;
        Scanner sc = new Scanner(System.in);   //为空,控制台 输入请求信息
        do {
            log.info("请输入客户端地址:==");
            address = sc.nextLine();
            log.info("请输入客户端生成的token:==");
            Token = sc.nextLine();
            map.put("Authorization", Token);
            log.info("正在发起连接");
            response = netUtils.get(address, null, map);
        } while (response.code() < 200 || response.code() > 300);
        //配置文件为空,将用户输入正确的配置信息,存储的文件
        upDataConnectionConfig(new ConnectionConfig(address,Token));
        //将写入返回的id值
        id=(String)response.data();
    }

    /**
     * 更新配置文件
     * @param connectionConfig 配置信息
     */
    public void upDataConnectionConfig(ConnectionConfig connectionConfig){
        File config = new File("config/server.json");
        if(!config.exists()){
            config.getParentFile().mkdirs();
        }
        try (FileWriter fileWriter=new FileWriter(config)){
            fileWriter.write(JSONObject.toJSONString(connectionConfig));
        }catch (Exception e ){
            log.error("写入失败,请检查");
        }
    }
    /**
     * 获取本地的连接配置
     * @return
     */
    public ConnectionConfig getLocalConnectionConfig() {
        File config = new File("config/server.json");
        if (config.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(config)) {
                String fig = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);
                return JSONObject.parseObject(fig, ConnectionConfig.class);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
