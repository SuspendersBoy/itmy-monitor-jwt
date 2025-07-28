<script lang="ts" setup>
import {onBeforeUnmount, onMounted, reactive, ref} from 'vue'
import {ElMessage, FormInstance, FormRules} from 'element-plus'
import "xterm/css/xterm.css"
const ruleFormRef = ref<FormInstance>()
import {post} from "@/net";
import {Terminal} from "xterm";
const terminalRef=ref()
const props=defineProps({
  clientId:{
    type:String
  },
  is:{
    type:Boolean
  }
})

const ip = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请输入ip地址'))
  } else if(ruleForm.ip.length<10){
    callback(new Error('请输入正确的ip地址'))
  }
  callback()
}

const port = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请输入正确的端口'))
  }
  callback()
}

const username = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else if (ruleForm.username.length<3 || ruleForm.username.length>16) {
    callback(new Error("请输入正确位数的用户名 3-16"))
  } else {
    callback()
  }
}

const password = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (ruleForm.password.length<6 || ruleForm.password.length>16) {
    callback(new Error("请输入正确位数的密码 6-16"))
  } else {
    callback()
  }
}
const ruleForm = reactive({
  ip: '',
  port:'',
  username: '',
  password: '',
  clientId:''
})
const rules = reactive<FormRules<typeof ruleForm>>({
  ip: [{ validator: ip, trigger: 'blur' }],
  username: [{ validator: username, trigger: 'blur' }],
  password: [{ validator: password, trigger: 'blur' }],
  port: [{ validator: port, trigger: 'blur' }],
})
const submitForm = () => {
  ruleFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      ruleForm.clientId=props.clientId
      post("/api/monitor/ssh-save",ruleForm,()=>{
        ElMessage.success("服务器注册成功")
      })
    }
  });
};

const socket = new WebSocket(`ws://localhost:8080/websocket?id=${props.clientId}`);
const term = new Terminal({
  lineHeight: 1.2,
  rows: 20,
  fontSize: 18,
  fontFamily: "Monaco, Menlo, Consolas, 'Courier New', monospace",
  fontWeight: "bold",
  theme: {
    background: '#000000'
  },
  cursorBlink: true,
  cursorStyle: 'underline',
  scrollback: 100,
  tabStopWidth: 4,
});

// 自定义输入缓冲区
let commandBuffer = '';

onMounted(() => {
  term.open(terminalRef.value);
  term.focus();

  // 显示初始命令提示符
  term.write('\x1b[32m$ \x1b[0m'); // 绿色的 $ 提示符

  // 处理键盘输入
  term.onKey(({ key, domEvent }) => {
    const event = domEvent;

    // 处理回车键
    if (event.key === 'Enter') {
      // 发送命令到服务端（包含换行符）
      socket.send(commandBuffer + '\n');

      // 本地回显换行
      term.write('\r\n');

      // 清空命令缓冲区
      commandBuffer = '';

      // 显示新的命令提示符
      term.write('\x1b[32m$ \x1b[0m');

      // 处理退格键
    } else if (event.key === 'Backspace') {
      if (commandBuffer.length > 0) {
        // 删除缓冲区最后一个字符
        commandBuffer = commandBuffer.slice(0, -1);

        // 终端回显退格效果
        term.write('\x1b[D \x1b[D'); // 光标左移，覆盖空格，再左移
      }

      // 处理 Tab 键（自动补全）
    } else if (event.key === 'Tab') {
      // 简单实现：添加两个空格（实际项目中可以实现命令补全）
      commandBuffer += '  ';
      term.write('  ');

      // 处理 Ctrl+C（中断当前命令）
    } else if (event.ctrlKey && event.key.toLowerCase() === 'c') {
      commandBuffer = '';
      socket.send('\x03'); // 发送 Ctrl+C 字符
      term.write('^C\r\n\x1b[32m$ \x1b[0m');

      // 处理可见字符
    } else if (key.length === 1 && !event.ctrlKey && !event.altKey && !event.metaKey) {
      // 添加到命令缓冲区
      commandBuffer += key;
      // 终端回显字符
      term.write(key);
    }
  });

  // 处理服务端返回的消息
  socket.onmessage = (event) => {
    // 直接写入终端
    term.write(event.data);
  };
});
onBeforeUnmount(()=>{
  socket.close()
  term.dispose()
})
</script>

<template>
  <div>
    <div v-show="props.is">
      <el-form ref="ruleFormRef" style="max-width: 600px" :model="ruleForm" status-icon :rules="rules" label-width="auto" class="demo-ruleForm">

      <el-form-item label="ip" prop="ip">
        <el-input v-model="ruleForm.ip" autocomplete="off"  placeholder="服务器ip地址" />
      </el-form-item>

        <el-form-item label="port" prop="port">
          <el-input v-model.number="ruleForm.port" placeholder="端口号"/>
        </el-form-item>

      <el-form-item label="用户名" prop="username">
        <el-input v-model="ruleForm.username" autocomplete="off" placeholder="用户名"/>
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input v-model.number="ruleForm.password" placeholder="密码"/>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm(ruleFormRef)" style="margin: 0 auto" >
          添加主信息
        </el-button>
      </el-form-item>

    </el-form>
    </div>
    <div ref="terminalRef" class="xterm " v-show="!props.is">

    </div>
  </div>
</template>

<style scoped>


</style>

