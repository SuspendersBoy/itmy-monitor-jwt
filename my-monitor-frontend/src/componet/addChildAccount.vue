<script  setup>
import { reactive, ref } from 'vue'
import {get, post} from "@/net";
import {ElMessage} from "element-plus";
const list = ref([]);
const ruleFormRef = ref()
let show=ref(false)
const childAccount=ref([])
const ruleForm = reactive({
  client:'',
  username: '',
  password: '',
})

const rules = reactive({
  username: [{ validator: validatePass2, trigger: 'blur' }],
  password: [{ validator: validatePass, trigger: 'blur' }]
})

const onSubmit = (ruleFormRef) => {
  post("/user/add-sub-account",ruleFormRef,()=>{
      ElMessage.success("添加成功")
  },()=>{
    ElMessage.error("添加用户失败->权限不足")
  })
}

const validatePass = (rule,value,callback) => {
  if (value === '') {
    show.value=true
    callback(new Error('密码不能为空'))
  }else if(value.length <6 || value.length > 12){
    show.value=true
    callback(new Error('密码不符合规范,请输入6~12位的用户名'))
  }else{
    show.value=false
    callback()
  }
}

const validatePass2 = (rule, value,callback) => {
  if (value === '') {
    show.value=true
    callback(new Error('请输入用户名'))
  }else if(value.length <6 || value.length > 16){
    show.value=true
    callback(new Error('用户名不符合规范,6~16位的密码'))
  }else{
    show.value=false
    callback()
  }
}

const upDateList = () => {
  get('/api/monitor/list',data => childAccount.value=data)
}
</script>
<template>
  <div>
    <el-form
        ref="ruleFormRef"
        style="max-width: 600px"
        :model="ruleForm"
        status-icon
        :rules="rules"
        label-width="auto"
        class="demo-ruleForm"
    >
      <el-form-item label="用户名" prop="name">
        <el-input v-model="ruleForm.username" />
      </el-form-item>

      <el-form-item label="密码" prop="pass">
        <el-input v-model="ruleForm.password" type="password" autocomplete="off" />
      </el-form-item>

      <el-form-item label="服务器">
        <el-select  @click="upDateList"  v-model="ruleForm.client" placeholder="分配权限服务器">
          <el-option v-for="item in childAccount" :value="item.clientId" :label="item.ip"/>
        </el-select>
        <el-button style="margin-left: 40px" type="primary" @click="onSubmit(ruleForm)" :disabled="show">
          提交
        </el-button>

      </el-form-item>

    </el-form>
  </div>
</template>
<style scoped>

</style>