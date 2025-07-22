package com.example.entity.vo.request;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RuntimeDetailVO {
    private String  clientId;
    private long timestamp;
    private double cpuUsage;
    private double memoryUsage;
    private double diskUsage;
    private double networkUpload;
    private double networkDownload;
    private double diskRead;
    private double diskWrite;
}
