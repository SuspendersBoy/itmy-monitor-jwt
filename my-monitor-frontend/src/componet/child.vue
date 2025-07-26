<script setup>
const props=defineProps({
  data:{}
});
</script>
<template>

    <div style="text-align: center; background-color:var(--el-border-color-dark);  border-radius:10px 30px;margin: 0 5px;font-size: 14px">
      <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
      <div > 用户名:{{data.username }}</div>
      <div > 角色:{{data.role}}</div>
      <!-- 渲染 data 里的内容，假设 data 有 userName 字段 -->
    </div>

</template>
<style scoped>

</style>