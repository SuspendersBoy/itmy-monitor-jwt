<script setup>
import {computed, ref} from "vue";

const dark = ref(useDark())

import {logout} from '@/net'
import router from "@/router";
import {useDark} from "@vueuse/core";
import {Moon, Sunny} from "@element-plus/icons-vue";
import TabItem from "@/componet/TabItem.vue";
import {useRoute,useRouter} from "vue-router";

function userLogout() {
  logout(() => router.push("/"))
}

const tabs=[
  {id: 1,name: '服务器列',route:'List'},
  {id: 2,name: '账户管理',route:'Manage'}
]

const route= useRoute();

const rou=useRouter();

const index = ()=>{
  for (let tab of tabs) {
    if(route.name === tab.route){
      return tab.id
    }
  }
  return 1
}

const tab=ref(index())

function changePage(item){
  tab.value=item.id
  rou.push({name: item.route})
}
</script>

<template>
  <el-container class="main-container">
    <el-header class="main-header">
      <el-image style="height: 55px" src="https://www.itbaima.cn/image/welcome/logo/dark.webp"></el-image>
      <div class="tabs">

        <tab-item v-for="item in tabs" :name="item.name" :active="item.id === tab" @click="changePage(item)"/>

        <el-switch style="margin:0 50px "
                   v-model="dark" active-color="#424242"
                   :active-action-icon="Moon"
                   :inactive-action-icon="Sunny">

        </el-switch>
        <el-dropdown >
          <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
         <template #dropdown>
           <el-dropdown-menu>
             <el-dropdown-item @click="userLogout">
               退出登入
             </el-dropdown-item>
           </el-dropdown-menu>
         </template>
        </el-dropdown>
      </div>
    </el-header>



    <el-main class="main-content" >

      <router-view v-slot="{ Component }">
        <transition name="el-fade-in-linear" mode="out-in">
          <component :is="Component"/>
        </transition>
      </router-view>

    </el-main>
  </el-container>


</template>



<style scoped>
.dark .main-container .main-content {
  background-color: #232323;
}
.main-content{
  height: 100%;
  background-color: #edebeb;
}
.tabs {
  height: 55px;
  gap: 10px;
  flex: 1px;
  display: flex;
  align-items: center;
  justify-content: right;
}

.main-container {
  height: 100vh;
  width: 100vw;
}

.main-header {
  height: 55px;
  background-color: #b7b7aa ;
  border-bottom: solid 1px var(--el-border-color);
  display: flex;
  align-items: center;
}
</style>
