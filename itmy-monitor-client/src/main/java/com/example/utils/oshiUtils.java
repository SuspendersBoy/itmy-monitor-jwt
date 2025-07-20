package com.example.utils;

import com.example.entity.BaseDetail;
import com.example.entity.RuntimeDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.*;
import java.io.File;
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
     * 1. 硬件信息（系统 CPU、内存、磁盘、显卡）
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

        }
        try {

            baseDetail.setOsArch(System.getProperty("os.arch"))
                    .setOsName(System.getProperty("os.name"))
                    .setOsVersion(System.getProperty("os.version"))
                    .setOsBit(os.getBitness())
                    .setCpuName(hardware.getProcessor().getProcessorIdentifier().getName())
                    .setCpuCore(hardware.getProcessor().getLogicalProcessorCount())
                    .setMemory((double) hardware.getMemory().getTotal() / Math.pow(1024.0, 3))
                    .setDisk(Arrays.stream(File.listRoots()).mapToLong(File::getTotalSpace).sum() / Math.pow(1024.0, 3))
                    .setIp(ip.getIPv4addr()[0]);
        } catch (Exception e) {
            return null;
        }


        return baseDetail;
    }

    public RuntimeDetail getRuntimeDetail() {
        RuntimeDetail detail = new RuntimeDetail();
        try {
            HardwareAbstractionLayer hardware = systemInfo.getHardware();
            OperatingSystem os = systemInfo.getOperatingSystem();

            detail.setTimestamp(System.currentTimeMillis());

            // 1. CPU 使用率（系统总体）
            CentralProcessor processor = hardware.getProcessor();
            // 第一次获取 ticks
            long[] prevTicks = processor.getSystemCpuLoadTicks();
            try {
                // 等待 100 毫秒，给 CPU 产生 ticks 变化的时间
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // 第二次基于间隔后的 ticks 计算使用率
            double cpuUsage = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
            detail.setCpuUsage(cpuUsage);

            // 2. 内存使用率
            GlobalMemory memory = hardware.getMemory();
            double memoryUsage = ((double) (memory.getTotal() - memory.getAvailable()) / memory.getTotal()) * 100;
            detail.setMemoryUsage(memoryUsage);

            // 3. 磁盘使用率（取所有非虚拟磁盘的总使用率）
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
            detail.setDiskUsage(diskUsage);

            // 4. 网络上下行流量（取所有网络接口的总和）
            long networkUpload = 0;
            long networkDownload = 0;
            for (NetworkIF net : hardware.getNetworkIFs()) {
                net.updateAttributes(); // 更新网络接口统计信息
                networkUpload += net.getBytesSent();
                networkDownload += net.getBytesRecv();
            }
            detail.setNetworkUpload(networkUpload);
            detail.setNetworkDownload(networkDownload);

            // 5. 磁盘读写（取所有磁盘的总和）
            long diskRead = 0;
            long diskWrite = 0;
            for (HWDiskStore disk : hardware.getDiskStores()) {
                disk.updateAttributes(); // 更新磁盘统计信息
                diskRead += disk.getReadBytes();
                diskWrite += disk.getWriteBytes();
            }
            detail.setDiskRead(diskRead);
            detail.setDiskWrite(diskWrite);

        }catch (Exception e){
            return null;
        }
        return detail;
    }

}