<script setup>
import child from "@/componet/child.vue"
import {reactive, ref} from "vue";
import {Lock, Switch} from '@element-plus/icons-vue'
import {get, logout, post} from "@/net";
import {ElMessage} from "element-plus";
import AddChildAccount from "@/componet/addChildAccount.vue";
import Child from "@/componet/child.vue";
import {useRoute} from "vue-router";
const  router=useRoute()
const childAccount = ref([])
let valid=ref(true)
const onValidate = (isValid) => {
  if (form.password!=='' && form.new_password!=='' && form.new_password_repeat!=='') {
    valid.value= !isValid
  }
}
const form = reactive({
  password: '',
  new_password: '',
  new_password_repeat: '',
})
const validatePassword = (rule, value, callback) => {
  if (value === '') {
    valid.value=true
    callback(new Error('请再次输入密码'))
  } else if (value !== form.new_password) {
    valid.value=true
    callback(new Error("两次输入的密码不一致"))
  }
}

const rules = {
  password: [
    { required: true, message: '请输入原来的密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur'] }
  ],
  new_password: [
    { required: true, message: '请输入新的密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur'] }
  ],
  new_password_repeat: [
    { required: true, message: '请重复输入新的密码', trigger: 'blur' },
    { validator: validatePassword, trigger: ['blur', 'change'] },
  ]
}
function resetPassword() {
  post("/user/change-password", form, data => {
     ElMessage.success("修改密码成功")
     logout(()=> router.push("/"))
  })
}
const creatAccount=ref(false)
function showElDrawer(){
  creatAccount.value=!creatAccount.value
}
function qChildAccount() {
  get("/user/select-sub-account", data =>{
    childAccount.value=data
  },()=>{
    ElMessage.error("获取子用户失败->  权限不足")
  })
}
if(router.name==='Manage'){
  qChildAccount()
}
</script>
<template>
  <div style="display: flex;gap: 10px" >
    <div class="info-car" style="flex: 50%">
      <div class="info-car" >
        <div style="text-align: center">
          <h3 >修改密码</h3>
        </div>
        <el-divider />
        <el-form @validate="onValidate" :model="form" :rules="rules"
                 ref="formRef" style="margin: 20px" label-width="100">
          <el-form-item label="当前密码" prop="password">
            <el-input type="password" v-model="form.password"
                      :prefix-icon="Lock" placeholder="当前密码" maxlength="16"/>
          </el-form-item>
          <el-form-item label="新密码" prop="new_password">
            <el-input type="password" v-model="form.new_password"
                      :prefix-icon="Lock" placeholder="新密码" maxlength="16"/>
          </el-form-item>
          <el-form-item label="重复新密码" prop="new_password_repeat">
            <el-input type="password" v-model="form.new_password_repeat"
                      :prefix-icon="Lock" placeholder="重复新密码" maxlength="16"/>
          </el-form-item>
          <div style="text-align: center">
            <el-button :icon="Switch" @click="resetPassword"
                       type="success" :disabled="valid">立即重置密码</el-button>
          </div>
        </el-form>
      </div>
      <div class="info-car" >

      </div>
    </div>
    <div class="info-car" style="flex: 50%">
      <div class="title" style="text-align: center ; margin-top: 20px">
        <i class="fa-solid fa-users"> 子用户管理</i>
      </div>
      <el-divider />
      <el-empty v-if="childAccount.length<=0" :image-size="100" description="还没有任何子用户哦">
      </el-empty>
      <el-drawer v-model="creatAccount"><addChildAccount/></el-drawer>
      <div style="display: flex ; gap: 10px">
        <child v-for="item in childAccount" :data="item" />
      </div>
      <el-button style="display:block;margin: 50px  auto;" type="primary" plain @click="showElDrawer">添加子用户</el-button>
    </div>
  </div>

</template>
<style scoped>
.info-car{
  border-radius: 7px;
  background-color: var(--el-fill-color-extra-light);
}
</style>