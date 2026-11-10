<template>
  <div class="account-setting-page">
    <div class="header">
      <button class="back-btn" @click="$router.back()">← 返回</button>
      <h2>账号设置</h2>
    </div>

    <div class="form-section">
      <div class="form-item">
        <label>用户名</label>
        <input type="text" v-model="newUsername" placeholder="请输入新用户名" />
      </div>
      <button class="btn save-btn" @click="updateUsername">保存用户名</button>

      <div class="form-item">
        <label>原密码</label>
        <input type="password" v-model="oldPassword" placeholder="请输入原密码" />
      </div>
      <div class="form-item">
        <label>新密码</label>
        <input type="password" v-model="newPassword" placeholder="请输入新密码" />
      </div>
      <div class="form-item">
        <label>确认新密码</label>
        <input type="password" v-model="confirmPassword" placeholder="请再次输入新密码" />
      </div>
      <button class="btn save-btn" @click="updatePassword">修改密码</button>

      <button class="btn delete-btn" @click="deleteAccount">注销账号</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const users = ref([])
const currentUser = ref('')

const newUsername = ref('')
const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

onMounted(() => {
  const d = localStorage.getItem('users')
  if (d) users.value = JSON.parse(d)
  currentUser.value = localStorage.getItem('loginUser') || ''
})

// 修改用户名
const updateUsername = () => {
  if (!newUsername.value) return alert('用户名不能为空')
  const user = users.value.find(u => u.username === currentUser.value)
  if (user) {
    // 更新用户数据
    const oldName = currentUser.value
    user.username = newUsername.value
    localStorage.setItem('users', JSON.stringify(users.value))
    // 更新登录状态
    localStorage.setItem('loginUser', newUsername.value)
    alert('用户名修改成功，请重新登录')
    router.push('/mine')
  }
}

// 修改密码
const updatePassword = () => {
  const user = users.value.find(u => u.username === currentUser.value)
  if (!user) return alert('用户不存在')
  if (user.password !== oldPassword.value) return alert('原密码错误')
  if (newPassword.value !== confirmPassword.value) return alert('两次新密码不一致')
  if (!newPassword.value) return alert('新密码不能为空')

  user.password = newPassword.value
  localStorage.setItem('users', JSON.stringify(users.value))
  alert('密码修改成功，请重新登录')
  localStorage.removeItem('loginUser')
  router.push('/mine')
}

// 注销账号
const deleteAccount = () => {
  if (!confirm('确定要注销账号吗？此操作不可恢复！')) return
  users.value = users.value.filter(u => u.username !== currentUser.value)
  localStorage.setItem('users', JSON.stringify(users.value))
  localStorage.removeItem('loginUser')
  alert('账号已注销')
  router.push('/mine')
}
</script>

<style scoped>
.account-setting-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding: 20px;
}
.header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.back-btn {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  margin-right: 10px;
}
.form-section {
  background: #fff;
  padding: 20px;
  border-radius: 10px;
}
.form-item {
  margin-bottom: 15px;
}
.form-item label {
  display: block;
  margin-bottom: 5px;
  font-size: 14px;
}
.form-item input {
  width: 100%;
  padding: 10px;
  border: 1px solid #eee;
  border-radius: 5px;
  box-sizing: border-box;
}
.btn {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-bottom: 10px;
}
.save-btn {
  background: #ff3333;
  color: #fff;
}
.delete-btn {
  background: #fff;
  color: #ff3333;
  border: 1px solid #ff3333;
}
</style>