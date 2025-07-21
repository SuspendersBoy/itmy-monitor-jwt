package com.example.utils;

import com.example.entity.BaseDetail;
import com.example.entity.RuntimeDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.*;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class oshiUtils {
    SystemInfo systemInfo = new SystemInfo();
    HardwareAbstractionLayer hardware = systemInfo.getHardware();
    OperatingSystem os = systemInfo.getOperatingSystem();

    /**
     * 1. 硬件信息（系统 CPU、内存、磁盘）
     */
    public BaseDetail printHardwareInfo() {
        System.out.println("=== 硬件配置信息 ===");
        BaseDetail baseDetail = new BaseDetail();

        List<NetworkIF> networks = hardware.getNetworkIFs();
        NetworkIF ip=null;
        for (NetworkIF network : networks) {
            try {
                String[] iPv4addr = network.getIPv4addr();
                NetworkInterface ni = network.queryNetworkInterface();
                if (!ni.isLoopback()
                        && !ni.isPointToPoint()
                        && ni.isUp()
                        && !ni.isVirtual()
                        && (ni.getName().startsWith("eth") || ni.getName().startsWith("en"))
                        && iPv4addr.length > 0) {
                    ip = network;
                }
            } catch (SocketException e) {
                log.error("读取网络信息出错");
                return null;
            }

        }// 内存总量（GB），保留两位小数
        double memoryGb = BigDecimal.valueOf((double) hardware.getMemory().getTotal() / Math.pow(1024.0, 3))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

// 磁盘总量（GB），保留两位小数
        double diskGb = BigDecimal.valueOf(Arrays.stream(File.listRoots())
                        .mapToLong(File::getTotalSpace)
                        .sum() / Math.pow(1024.0, 3))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        try {
            if (ip != null) {
                baseDetail.setOsArch(System.getProperty("os.arch"))
                        .setOsName(System.getProperty("os.name"))
                        .setOsVersion(System.getProperty("os.version"))
                        .setOsBit(os.getBitness())
                        .setCpuName(hardware.getProcessor().getProcessorIdentifier().getName())
                        .setCpuCore(hardware.getProcessor().getLogicalProcessorCount())
                        .setMemory(memoryGb)
                        .setDisk(diskGb)
                        .setIp(ip.getIPv4addr()[0]);
            }
        } catch (Exception e) {
            return null;
        }


        return baseDetail;
    }

    /**
     * 获取实时数据
     * @return
     */
    public RuntimeDetail getRuntimeDetail() {
        RuntimeDetail detail = new RuntimeDetail();
        try {
            HardwareAbstractionLayer hardware = systemInfo.getHardware();
            OperatingSystem os = systemInfo.getOperatingSystem();

            detail.setTimestamp(System.currentTimeMillis());

            // 1. CPU 使用率（系统总体）
            CentralProcessor processor = hardware.getProcessor();

// 采样次数和间隔（毫秒）
            int samples = 2;
            int interval = 500;

// 多次采样计算平均值
            double totalCpuUsage = 0;
            long[] prevTicks = processor.getSystemCpuLoadTicks();

            for (int i = 0; i < samples; i++) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                long[] currentTicks = processor.getSystemCpuLoadTicks();
                double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks);

                // 过滤无效值（首次调用可能返回 -1.0）
                if (cpuLoad >= 0) {
                    totalCpuUsage += cpuLoad;
                }

                prevTicks = currentTicks;
            }

// 计算平均 CPU 使用率并转为百分比
            double avgCpuUsage = (totalCpuUsage / samples) * 100;

// 保留两位小数
            avgCpuUsage = BigDecimal.valueOf(avgCpuUsage)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();

            detail.setCpuUsage(avgCpuUsage);

            // 2. 内存使用量（GB）
            GlobalMemory memory = hardware.getMemory();
            double memoryUsed = (memory.getTotal() - memory.getAvailable()) / Math.pow(1024.0, 3);
            memoryUsed = BigDecimal.valueOf(memoryUsed)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            detail.setMemoryUsage(memoryUsed); // 假设存在 setMemoryUsed 方法

            // 3. 磁盘使用率（取所有非虚拟磁盘的总使用率）
            double totalDiskUsed = 0;
            double totalDiskSize = 0;
            FileSystem fileSystem = os.getFileSystem();
            for (HWDiskStore disk : hardware.getDiskStores()) {
                for (HWPartition partition : disk.getPartitions()) {
                    // 遍历文件存储，找到匹配挂载点的
                    for (OSFileStore fileStore : fileSystem.getFileStores()) {
                        if (fileStore.getMount().equals(partition.getMountPoint())) {
                            totalDiskUsed += fileStore.getTotalSpace() - fileStore.getUsableSpace();
                            totalDiskSize += fileStore.getTotalSpace();
                            break; // 找到对应存储，退出内层循环
                        }
                    }
                }
            }
            double diskUsage = (totalDiskSize > 0) ? (totalDiskUsed / totalDiskSize) * 100 : 0;
            diskUsage = BigDecimal.valueOf(diskUsage)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            detail.setDiskUsage(diskUsage);

// 4. 网络上下行流量（单位：KB/s）
            long initialUpload = 0;
            long initialDownload = 0;

// 首次采样
            for (NetworkIF net : hardware.getNetworkIFs()) {
                net.updateAttributes();
                initialUpload += net.getBytesSent();
                initialDownload += net.getBytesRecv();
            }

// 等待1秒，获取流量变化
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            long finalUpload = 0;
            long finalDownload = 0;

// 再次采样
            for (NetworkIF net : hardware.getNetworkIFs()) {
                net.updateAttributes();
                finalUpload += net.getBytesSent();
                finalDownload += net.getBytesRecv();
            }

// 计算1秒内的流量变化（KB/s）
            double uploadSpeed = (finalUpload - initialUpload) / 1024.0;
            double downloadSpeed = (finalDownload - initialDownload) / 1024.0;

// 保留两位小数
            uploadSpeed = BigDecimal.valueOf(uploadSpeed)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            downloadSpeed = BigDecimal.valueOf(downloadSpeed)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();

            detail.setNetworkUpload(uploadSpeed);  // 单位：KB/s
            detail.setNetworkDownload(downloadSpeed);  // 单位：KB/s

            // 5. 磁盘读写（取所有磁盘的总和）
            double diskRead = 0;
            double diskWrite = 0;
            for (HWDiskStore disk : hardware.getDiskStores()) {
                disk.updateAttributes(); // 更新磁盘统计信息
                diskRead += disk.getReadBytes();
                diskWrite += disk.getWriteBytes();
            }
            diskRead = BigDecimal.valueOf(diskRead)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            diskWrite = BigDecimal.valueOf(diskWrite)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            detail.setDiskRead(diskRead);
            detail.setDiskWrite(diskWrite);

        }catch (Exception e){
            return null;
        }
        return detail;
    }

}