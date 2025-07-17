package com.example.utils;

import com.example.entity.BaseDetail;
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
    public  BaseDetail printHardwareInfo() {
        System.out.println("=== 硬件配置信息 ===");


        List<NetworkIF> networks = hardware.getNetworkIFs();
        NetworkIF ip=null;
        for (NetworkIF network : networks) {
            try {
                String[] iPv4addr=network.getIPv4addr();
                NetworkInterface ni =network.queryNetworkInterface();
                if (!ni.isLoopback()
                        && !ni.isPointToPoint()
                        && ni.isUp()
                        && !ni.isVirtual()
                        && (ni.getName().startsWith("eth") || ni.getName().startsWith("en"))
                        && iPv4addr.length > 0) {
                    ip=network;
                }
            } catch (SocketException e) {
                log.error("读取网络信息出错");
                ip=null;
            }

        }

      return  new BaseDetail()
                .setOsArch(System.getProperty("os.arch"))
                .setOsName(System.getProperty("os.name"))
                .setOsVersion(System.getProperty("os.version"))
                .setOsBit(os.getBitness())
                .setCpuName(hardware.getProcessor().getProcessorIdentifier().getName())
                .setCpuCore(hardware.getProcessor().getLogicalProcessorCount())
                .setMemory((double) hardware.getMemory().getTotal() /Math.pow(1024.0, 3))
                .setDisk(Arrays.stream(File.listRoots()).mapToLong(File::getTotalSpace).sum( ) /Math.pow(1024.0, 3))
                .setIp(ip.getIPv4addr()[0]);
    }

//    /**
//     * 2. 操作系统信息（版本、启动时间、位数）
//     */
//    private static void printOSInfo(OperatingSystem os) {
//        System.out.println("\n=== 操作系统配置信息 ===");
//        System.out.println("系统名称: " + os.getName());
//        System.out.println("系统版本: " + os.getVersionInfo().toString());
//        System.out.println("系统位数: " + os.getBitness() + " 位");
//        System.out.println("系统启动时间: " + FormatUtil.formatDateTime(os.getSystemBootTime()));
//        System.out.println("系统运行时间: " + FormatUtil.formatElapsedSecs(os.getSystemUptime()));
//    }
//
//    /**
//     * 3. 进程信息（CPU 占用前 5 的进程）
//     */
//    private static void printProcessInfo(OperatingSystem os) {
//        System.out.println("\n=== 进程配置信息（前 5） ===");
//        List<OSProcess> processes = os.getProcesses(
//                OperatingSystem.ProcessFiltering.VALID_PROCESS,
//                OperatingSystem.ProcessSorting.CPU_DESC,
//                5
//        );
//
//        processes.forEach(process -> {
//            System.out.println("\n进程名称: " + process.getName());
//            System.out.println("进程 PID: " + process.getProcessID());
//            System.out.println("CPU 占用: " + String.format("%.2f%%", process.getProcessCpuLoadCumulative() * 100));
//            System.out.println("内存占用: " + FormatUtil.formatBytes(process.getResidentSetSize()));
//        });
//    }
//
//    /**
//     * 4. 网络信息（IP、MAC、带宽）
//     */
//    private static void printNetworkInfo(HardwareAbstractionLayer hardware, OperatingSystem os) {
//        System.out.println("\n=== 网络配置信息 ===");
//
//        // 网络接口信息
//        List<NetworkIF> networks = hardware.getNetworkIFs();
//        networks.forEach(net -> {
//            System.out.println("\n网卡名称: " + net.getName());
//            System.out.println("MAC 地址: " + net.getMacaddr());
//            System.out.println("IP 地址: " + net.getIPv4addr());
//            System.out.println("上传带宽: " + FormatUtil.formatBytes(net.getBytesSent()) + "/s");
//            System.out.println("下载带宽: " + FormatUtil.formatBytes(net.getBytesReceived()) + "/s");
//        });
//
//        // 网络协议统计（TCP/UDP 连接数）
//        InternetProtocolStats ipStats = os.getInternetProtocolStats();
//        System.out.println("\nTCP 连接数: " + ipStats.getTcpConnections());
//        System.out.println("UDP 数据包数: " + ipStats.getUdpDatagramsReceived());
//    }
//
//    /**
//     * 5. 文件系统信息（磁盘分区、使用率）
//     */
//    private static void printFileSystemInfo(OperatingSystem os) {
//        System.out.println("\n=== 文件系统配置信息 ===");
//        FileSystem fileSystem = os.getFileSystem();
//        fileSystem.getMountedFilesystems().forEach(fs -> {
//            System.out.println("\n分区路径: " + fs.getMountPoint());
//            System.out.println("文件系统类型: " + fs.getType());
//            System.out.println("总容量: " + FormatUtil.formatBytes(fs.getTotalSpace()));
//            System.out.println("已用容量: " + FormatUtil.formatBytes(fs.getTotalSpace() - fs.getFreeSpace()));
//            System.out.println("使用率: " + String.format("%.2f%%", fs.getUsage() * 100));
//        });
//    }
}