package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("db_client_detail")
public class BaseDetailDto {
    @TableId(type=IdType.AUTO)
    private Integer id;
    private String osArch;
    private String osName;
    private String osVersion;
    private int osBit;
    private String cpuName;
    private int cpuCore;
    private double memory;
    private double disk;
    private String ip;
    private String clientId;
}
