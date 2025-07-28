<script setup>
import {useClipboard} from "@vueuse/core";

const props= defineProps({
  data: Object
})
// 从 useClipboard 中解构出 copy 方法
const { copy } = useClipboard();

// 定义复制 IP 的函数
const copyIp = () => {
  // 调用 copy 方法复制 IP 地址，复制成功后执行 then 里的逻辑
  copy(props.data.ip).then(() => {
    ElMessage.success('成功复制IP地址到剪贴板');
  });
};
</script>

<template>
  <div class="instance-card">
    <div style="display: flex;justify-content: space-between">
      <div>
        <div class="name">
          <span class="flag-icon flag-icon-cn"></span>
          <span style="margin: 0 5px">超原子核能服务器</span>
          <i style="color: #da2323;padding: 0 0 0 60px" class="fa-solid fa-pause" v-show="!props.data.online"></i>
          <i style="color: #18cb18;padding: 0 0 0 60px"  class="fa-solid fa-circle-play" v-show="props.data.online"></i>
          <span style="margin-left: 5px; font-size: 14px">  {{ props.data.online ? '运行中' : '已终止' }}</span>
        </div>
      </div>
    </div>
    <div class="status" style="margin:15px 0">
      {{props.data.osName}} : {{props.data.osVersion}}
    </div>
    <el-divider style="margin: 10px 0"/>
    <div class="network">
      <span style="margin-right: 10px">{{props.data.ip}}</span>
      <i class="fa-solid fa-copy interact-item" style="color: dodgerblue" @click.stop="copyIp"></i>
    </div>
    <div class="hardware">
      <i class="fa-solid fa-microchip"></i>
      <span style="margin-right: 10px">  {{props.data.cpuCore}} CPU</span>
      <i class="fa-solid fa-memory"></i>
      <span> {{props.data.memory}}  GB</span>
    </div>
    <div class="progress">
      <span>CPU:{{props.data.cpuUsage}} %</span>
      <el-progress :percentage="props.data.cpuUsage" :stroke-width="5" :show-text="false"/>
    </div>
    <div class="progress">
      <span>内存: <b>{{props.data.memoryUsage}}</b> GB</span>
      <el-progress :percentage="props.data.memoryUsage/64*100" :stroke-width="5" :show-text="false"/>
    </div>
    <div class="network-flow">
      <div>网络流量:</div>
      <div >
        <i class="fa-solid fa-arrow-up"></i>
        <span> {{props.data.networkUpload<=1024 ? props.data.networkUpload :( props.data.networkUpload/1024).toFixed(2)}}  {{props.data.networkUpload<=1024 ?'KB/s' : 'Mb/s'}}</span>
        <el-divider direction="vertical"/>
        <i class="fa-solid fa-arrow-down"></i>
        <span> {{props.data.networkDownload<=1024 ? props.data.networkDownload :( props.data.networkDownload/1024).toFixed(2)}}  {{props.data.networkDownload<=1024 ?'KB/s' : 'Mb/s'}}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.interact-item{
  transition: .3s;
  &:hover {
    cursor: pointer;
    scale: 1.2;
    opacity: 0.8;
  }
}
.progress{
  margin: 5px 0;
  font-size: 12px
}
.network-flow{
  display: flex;
  justify-content:space-between;
  margin: 10px 0;
  font-size: 12px
}
.hardware{
  margin: 5px 0;
  font-size: 12px
}
.hardware{
  font-size: 12px;
}
.network{
  font-size: 12px;
}
.instance-card {
  transition: .3s;
  &:hover {
    cursor: pointer;
    scale: 1.05;
    opacity: 0.8;
    border: 1px dotted #37a8ff;
  }
  width: 320px;
  padding: 15px;
  background-color: var(--el-bg-color);
  border-radius: 5px;
  box-sizing: border-box;

  .name {
    font-size: 15px;
    font-weight: bold;
  }

  .status {
    font-size: 12px;
  }
}
</style>