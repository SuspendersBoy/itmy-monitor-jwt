<script setup>
const props= defineProps({
  data: Object
})
</script>

<template>
  <div class="instance-card">
    <div style="display: flex;justify-content: space-between">
      <div>
        <div class="name">
          <span class="flag-icon flag-icon-cn"></span>
          <span style="margin: 0 5px">白马后端云服务器</span>
          <i class="fa-solid fa-pen-to-square"></i>
          <i style="color: #18cb18;padding: 0 0 0 60px"  class="fa-solid fa-circle-play" ></i>
          <span style="margin-left: 5px; font-size: 14px">运行中</span>
        </div>
      </div>
    </div>
    <div class="status" style="margin:15px 0">
      操作系统：Ubuntu 20.04
    </div>
    <el-divider style="margin: 10px 0"/>
    <div class="network">
      <span style="margin-right: 10px">公网IP: 192.168.0.11</span>
      <i class="fa-solid fa-copy" style="color: dodgerblue"></i>
    </div>
    <div class="hardware">
      <i class="fa-solid fa-microchip"></i>
      <span style="margin-right: 10px"> 2 CPU</span>
      <i class="fa-solid fa-memory"></i>
      <span> 4 GB</span>
    </div>
    <div class="progress">
      <span>CPU: 2.5%</span>
      <el-progress :percentage="2.5" :stroke-width="5" :show-text="false"/>
    </div>
    <div class="progress">
      <span>内存: <b>1.2</b> GB</span>
      <el-progress :percentage="1.2/4 * 100" :stroke-width="5" :show-text="false"/>
    </div>
    <div class="network-flow">
      <div>网络流量:</div>
      <div  >
        <i class="fa-solid fa-arrow-up"></i>
        <span> 52 KB/s</span>
        <el-divider direction="vertical"/>
        <i class="fa-solid fa-arrow-down"></i>
        <span> 256 KB/s</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.progress{
  margin: 5px 0;
  font-size: 12px
}
.network-flow{

  display: flex;
  gap: 105px; /* 间距 */
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

  .os {
    font-size: 13px;
  }
}
</style>