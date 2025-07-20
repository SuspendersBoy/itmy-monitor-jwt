package com.example.utils;

import com.example.entity.dto.RuntimeDataDto;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class InfluxDbUtils {
    @Value("${influxdb.influxdbUrl}")
    String influxdbUrl;
    @Value("${influxdb.username}")
    String username;
    @Value("${influxdb.password}")
    String password;
    @Value("${influxdb.bucket}")
    String bucket;
    @Value("${influxdb.org}")
    String org;
    InfluxDBClient client;

    /**
     * 时序数据库工具类
     */
    @PostConstruct
    private void init(){
        client=InfluxDBClientFactory.create(influxdbUrl,username,password.toCharArray());
    }

    public void writRuntimeData(String clientId, RuntimeDetailVO vo){
        RuntimeDataDto runtimeDataDto =new RuntimeDataDto();
        BeanUtils.copyProperties(vo, runtimeDataDto);
        runtimeDataDto.setTimestamp(new Date(vo.getTimestamp()).toInstant());
        runtimeDataDto.setId(clientId);

        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, runtimeDataDto);
    }
}
