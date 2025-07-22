package com.example.utils;

import com.example.entity.dto.RuntimeDataDto;
import com.example.entity.vo.request.RuntimeDetailVO;
import com.example.entity.vo.response.fluxClient;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    /**
     * 根据 ID 获取对应的实时数据列表
     *
     * @param clientId 要查询的 ID
     * @return 包含 RuntimeDataDto 数据的列表，可根据实际需求调整返回类型
     */
    public List<fluxClient> getRuntimeDataById(String clientId) {
        // Flux 查询语句，从指定 bucket 和 org 中查询数据，过滤条件为 id 等于 clientId
        String fluxQuery = String.format("from(bucket: \"%s\") " +
                        "|> range(start: -2h) " + // 查询最近 1 分钟的数据
                        "|> filter(fn: (r) => r._measurement == \"runtime\") " +
                        "|> filter(fn: (r) => r.id == \"%s\")",
                bucket, clientId);



        QueryApi queryApi = client.getQueryApi();
        List<FluxTable> tables = queryApi.query(fluxQuery, org);

        List<fluxClient> lists=new ArrayList<>();

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                if (Objects.equals(record.getValueByKey("_field"), "cpuUsage")) {
                    fluxClient f = new fluxClient();
                    f.setCpuUsage(record.getValueByKey("_value"));
                    f.setData(Date.from((Instant) record.getValueByKey("_time")));
                    lists.add(f);
                }

            }
        }


     return lists;
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
