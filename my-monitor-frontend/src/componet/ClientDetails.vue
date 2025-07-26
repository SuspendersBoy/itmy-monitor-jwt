<script setup>
import {get} from "@/net";
import {ref} from 'vue';
import LineChart from "@/componet/LineChart.vue";
import { ElMessageBox } from 'element-plus'
import axios from "axios";
import {useRoute} from "vue-router";
const route= useRoute();
const dialogVisible = ref(false)
const props = defineProps({
  data: Object
})

const chartData = ref([]);
const clientItem = () => {
  if(route.name==='List') {
    const paramUrl = `/client/flux?id=${props.data.clientId}`
    get(paramUrl, data => chartData.value = data)
  }
}
setInterval(clientItem, 5000)

const deleteClient = async () => {
  const paramUrl = `/client/deleteClient?id=${props.data.clientId}`;
  await axios.delete(paramUrl);
  dialogVisible.value = false
  ElMessageBox.confirm('删除成功')
}
</script>
<template>
  <div class="titles">
    <div class="title-monitor">历史数据</div>
    <el-button plain @click="dialogVisible = true"  style="color: #ffffff ">
      删除主机
    </el-button>
    <el-divider style="margin: 15px 0"/>
    <el-progress type="dashboard" :width="100" :percentage="data.diskUsage" status="success">
      <div style="font-size: 12px;font-weight: bold;color: initial">储存使用率</div>
      <div style="font-size: 15px;color: grey;margin-top: 5px">{{ data.diskUsage }}%</div>
    </el-progress>
    <el-divider style="margin: 15px 0"/>
    <div class="page-container">
      <!-- 使用折线图组件 -->
      <LineChart v-if="chartData" :data="chartData" title="cpu趋势"/>
    </div>

    <div>
      <el-dialog v-model="dialogVisible" title="你确定删除主机吗?"  width="400" :before-close="handleClose">
        <span>请做好你的选择!</span>
        <span>主机id{{props.data.clientId}}</span>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="deleteClient" >确定</el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </div>
</template>
<style scoped>
.title-monitor {
  font-size: 32px;
  color: #37a8ff;
  text-align: center;
}

.titles {
  margin-bottom: 30px;
}

button {
  padding: 8px 16px;
  background-color: #809cd5;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #3280fc;
}
</style>