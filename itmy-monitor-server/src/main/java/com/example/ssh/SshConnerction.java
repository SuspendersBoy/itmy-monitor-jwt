package com.example.ssh;

import com.example.entity.dto.ClientSshDTO;
import com.example.mapper.SshMapper;
import com.jcraft.jsch.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SshConnerction {
    @Autowired
    SshMapper sshMapper;

    public Session sshCannnection(String clientId) throws JSchException {
        JSch jsch = new JSch();
        //获取当前clientId 的配置信息
        Map<String, Object> pram = new HashMap<>();
        pram.put("client_id", clientId);
        List<ClientSshDTO> clientShh = sshMapper.selectByMap(pram);
        ClientSshDTO clientSshDTO = clientShh.get(0);

        // 创建 Session，设置用户名、主机、端口（SSH 默认 22 ）
        Session session = jsch.getSession(clientSshDTO.getUsername(), clientSshDTO.getIp(), clientSshDTO.getPort());
        // 设置密码（也可用密钥方式，更安全 ）
        session.setPassword(clientSshDTO.getPassword());
        // 配置跳过主机密钥检查（生产环境慎用，建议校验密钥；这里为快速测试 ）
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        return session;
    }

    public void order(Channel channel, String Command) throws IOException, JSchException {
        // 获取输入/输出流
        OutputStream commandInput = channel.getOutputStream();
        // 执行第一条命令
        sendCommand(commandInput, Command);
    }

    // 发送命令（添加换行符）
    private void sendCommand(OutputStream outputStream, String command) throws IOException {
        outputStream.write((command + "\n").getBytes("UTF-8"));
        outputStream.flush();
    }


    // 读取输出
    public void readOutput(WebSocketSession session,InputStream reader) {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while (true) {
                try {
                    // 直接读取字节流（保留所有原始数据）
                    while ((bytesRead = reader.read(buffer)) != -1) {
                        // 转成字符串，转发给前端
                        String output = new String(buffer, 0, bytesRead);
                        session.sendMessage(new TextMessage(output));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
