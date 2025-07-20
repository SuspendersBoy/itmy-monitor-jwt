<script setup>
import Client from "@/componet/Client.vue";
import {ref} from "vue";
import {get} from "@/net";

const list=ref([])
const upDateList = () => get('/api/monitor/list',data => list.value=data)
setInterval(upDateList,10000)

</script>

<template>
  <div class="list-main">
    <div class="title">
      <div ><i class="fa-solid fa-server"></i> 管理主机列表</div>
      <div class="desc">在这里管理所有已经注册的主机实例，实时监控主机运行状态，快速进行管理和操作。</div>
    </div>
    <el-divider style="margin: 10px 0"/>
    <div class="list-card">
      <Client/>
      <Client/>
      <Client/>
      <Client/>
      <Client/>
      <Client/>
    </div>
  </div>
</template>

<style scoped>
.list-card {
  display: flex;
  flex-wrap: wrap; /* 自动换行 */
  gap: 110px;
}
.list-main {
  width: 100%;

  .title {
    font-size: 22px;
    font-weight: bold;
  }

  .desc {
    font-size: 15px;
    color: grey;
  }
}
</style>