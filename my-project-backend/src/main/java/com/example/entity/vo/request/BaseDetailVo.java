package com.example.entity.vo.request;

import lombok.Data;

@Data
public class BaseDetailVo {
    private String osArch;
    private String osName;
    private String osVersion;
    private int osBit;
    private String cpuName;
    private int cpuCore;
    private double memory;
    private double disk;
    private String ip;
}
