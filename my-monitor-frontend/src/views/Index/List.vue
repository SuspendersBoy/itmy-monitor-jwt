<script setup>
import Client from "@/componet/Client.vue";
import {reactive, ref} from "vue";
import {get} from "@/net";
import ClientDetails from "@/componet/ClientDetails.vue";
const list=ref([])
const details=reactive({
  detail:{}
})
const upDateList = () => get('/api/monitor/list',data => list.value=data)
setInterval(upDateList,5000)
upDateList()

// 定义响应式数据，控制抽屉显示状态和传递的 id
const detail = reactive({
  show: false,
});
// 携带参数示例，假设需要传递 id
const clientItem = (id) => {
  const paramUrl = `/client/getRuntimeById?id=${id}`;
  get(paramUrl, data => Object.assign(details.detail,data));
};

// 定义方法，用于触发抽屉显示并设置 id
const displayClientDetails = (id) => {
  detail.show = true;// 显示抽屉
  clientItem(id)
};
</script>

<template>
  <div class="list-main">
    <div class="title">
      <div ><i class="fa-solid fa-server"></i> 管理主机列表</div>
      <div class="desc">在这里管理所有已经注册的主机实例，实时监控主机运行状态，快速进行管理和操作。</div>
    </div>
    <el-divider style="margin: 10px 0"/>
    <div class="list-card">
      <Client v-for="item in list"  :data="item" @click="displayClientDetails(item.clientId)"/>
      <el-drawer size="520"  :show-close="false" v-model="detail.show" :with-header="false" v-if="list.length">
          <ClientDetails  :data="details.detail" ></ClientDetails>
      </el-drawer>
    </div>
  </div>
</template>

<style scoped>
/* 穿透作用域，修改 el-drawer 的样式 */
:deep(.el-drawer) {
  /* 设置外边距：上下左右各 10px（注意：margin 拼写错误，应该是 margin） */
  margin: 10px;

  /* 高度计算：100% 减去 20px（通常用于自适应高度） */
  height: calc(100% - 20px);

  /* 圆角：10px */
  border-radius: 10px;
}
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