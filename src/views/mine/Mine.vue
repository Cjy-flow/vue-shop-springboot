<template>
  <div class="mine-page">
    <!-- 未登录状态 -->
    <div class="unlogin" v-if="!isLogin">
      <div class="avatar-circle" @click="showLoginModal = true">
        <img src="https://picsum.photos/id/1005/100/100" alt="默认头像">
      </div>
      <button class="login-btn" @click="showLoginModal = true">登录 / 注册</button>
    </div>

    <!-- 已登录状态 -->
    <div class="login" v-else>
      <div class="user-card">
        <div class="avatar-wrapper">
          <div class="avatar-circle" @click="triggerFileUpload">
            <img :src="userInfo.avatar" alt="头像">
          </div>
          <div class="username">{{ userInfo.username }}</div>
        </div>

        <input type="file" ref="fileInput" style="display: none" accept="image/*" @change="handleAvatarChange" />
      </div>

      <div class="order-section">
        <div class="section-header">
          <span>我的订单</span>
          <span class="view-all" @click="goToOrderList">查看全部 ></span>
        </div>
        
        <!-- 🔥 改造后：图标 + 文字 + 点击跳转 -->
        <div class="order-tabs">
          <div class="tab" v-for="item in orderTabList" :key="item.key" @click="goToOrderTab(item.key)">
            <div class="tab-icon">{{ item.icon }}</div>
            <div class="tab-text">{{ item.name }}</div>
          </div>
        </div>
      </div>

      <div class="menu-list">
        <div class="menu-item" @click="$router.push('/collect')">⭐ 我的收藏</div>
        <div class="menu-item" @click="$router.push('/address-manage')">📍 收货地址管理</div>
        <div class="menu-item" @click="$router.push('/account-setting')">⚙️ 账号设置</div>
      </div>

      <button class="logout-btn" @click="logout">退出登录</button>
    </div>

    <!-- 登录弹窗 -->
    <div class="modal-overlay" v-if="showLoginModal" @click.self="showLoginModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <span @click="currentForm = 'login'" :class="{ active: currentForm === 'login' }">登录</span>
          <span @click="currentForm = 'register'" :class="{ active: currentForm === 'register' }">注册</span>
          <span @click="currentForm = 'forget'" class="forget-link">忘记密码？</span>
          <button class="close-btn" @click="showLoginModal = false">×</button>
        </div>

        <div class="modal-body" v-if="currentForm === 'login'">
          <input type="text" v-model="form.username" placeholder="用户名" />
          <input type="password" v-model="form.password" placeholder="密码" />
          <button @click="handleLogin">登录</button>
        </div>
        <div class="modal-body" v-if="currentForm === 'register'">
          <input type="text" v-model="form.username" placeholder="用户名" />
          <input type="password" v-model="form.password" placeholder="密码" />
          <input type="password" v-model="form.confirmPwd" placeholder="确认密码" />
          <button @click="handleRegister">注册</button>
        </div>
        <div class="modal-body" v-if="currentForm === 'forget'">
          <input type="text" v-model="form.username" placeholder="用户名" />
          <input type="password" v-model="form.newPassword" placeholder="新密码" />
          <input type="password" v-model="confirmNewPassword" placeholder="确认新密码" />
          <button @click="handleForgetPassword">重置密码</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

const router = useRouter()
const store = useStore()

const isLogin = ref(false)
const userInfo = reactive({
  username: '用户',
  avatar: 'https://picsum.photos/id/1005/100/100',
})
const fileInput = ref(null)

const triggerFileUpload = () => fileInput.value.click()
const handleAvatarChange = (e) => {
  const file = e.target.files[0]
  if (file) userInfo.avatar = URL.createObjectURL(file)
}

const showLoginModal = ref(false)
const currentForm = ref('login')
const form = reactive({
  username: '', password: '', confirmPwd: '', newPassword: '', confirmNewPassword: ''
})

const users = ref([])

const loadUsers = () => {
  const d = localStorage.getItem('users')
  if (d) users.value = JSON.parse(d)
}
const saveUsers = () => localStorage.setItem('users', JSON.stringify(users.value))
const saveLogin = (name) => localStorage.setItem('loginUser', name)
const clearLogin = () => localStorage.removeItem('loginUser')

onMounted(() => {
  loadUsers()
  const last = localStorage.getItem('loginUser')
  if (last) {
    isLogin.value = true
    userInfo.username = last
    store.commit('INIT_USER_DATA', last)
  }
})

// 🔥 已按你要求修改：文字 + 图标 + 跳转key
const orderTabList = ref([
  { key: 'pay', name: '待支付', icon: '💳' },
  { key: 'send', name: '待发货', icon: '📦' },
  { key: 'receive', name: '待收货', icon: '🚚' },
  { key: 'done', name: '已完成', icon: '✅' },
  { key: 'refund', name: '售后/退款', icon: '🔄' },
])

const goToOrderList = () => {
  router.push('/order-list')
}

// 🔥 点击跳转到订单对应标签
const goToOrderTab = (key) => {
  router.push({
    path: '/order-list',
    query: { type: key }
  })
}

const handleLogin = () => {
  const user = users.value.find(u => u.username === form.username && u.password === form.password)
  if (user) {
    isLogin.value = true
    userInfo.username = form.username
    saveLogin(form.username)
    store.commit('INIT_USER_DATA', form.username)
    showLoginModal.value = false
  } else alert('账号或密码错误')
}

const handleRegister = () => {
  if (!form.username || !form.password || form.password !== form.confirmPwd) return alert('信息错误')
  if (users.value.some(u => u.username === form.username)) return alert('用户名已存在')
  users.value.push({ username: form.username, password: form.password })
  saveUsers()
  alert('注册成功')
  currentForm.value = 'login'
}
const handleForgetPassword = () => {
  const user = users.value.find(u => u.username === form.username)
  if (!user) return alert('用户不存在')
  if (form.newPassword !== form.confirmNewPassword) return alert('两次密码不一致')
  user.password = form.newPassword
  saveUsers()
  alert('重置成功')
  currentForm.value = 'login'
}
const logout = () => {
  isLogin.value = false
  clearLogin()
}
</script>

<style scoped>
.mine-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 60px;
}
.avatar-circle {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  overflow: hidden;
  background: #eee;
  cursor: pointer;
}
.avatar-circle img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.unlogin {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 50px 0;
}
.login-btn {
  margin-top: 20px;
  padding: 10px 25px;
  background: #ff3333;
  color: #fff;
  border: none;
  border-radius: 20px;
  cursor: pointer;
}

.user-card {
  background: linear-gradient(135deg, #ff3333, #ff6666);
  color: #fff;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.username {
  margin-top: 10px;
  font-size: 18px;
  font-weight: bold;
}

.order-section {
  background: #fff;
  margin: 10px;
  padding: 15px;
  border-radius: 10px;
  cursor: pointer;
}
.section-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

/* 🔥 样式兼容原有布局 */
.order-tabs {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #666;
  text-align: center;
}
.tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}
.tab-icon {
  font-size: 18px;
  margin-bottom: 4px;
}
.tab-text {
  font-size: 12px;
}

.menu-list {
  background: #fff;
  margin: 10px;
  border-radius: 10px;
}
.menu-item {
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
  cursor: pointer;
}
.logout-btn {
  width: 90%;
  margin: 20px auto;
  padding: 12px;
  background: #fff;
  color: #ff3333;
  border: 1px solid #ff3333;
  border-radius: 8px;
  cursor: pointer;
}

.modal-overlay {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}
.modal-content {
  background: #fff;
  padding: 25px;
  border-radius: 10px;
  width: 80%;
  max-width: 400px;
  position: relative;
}
.modal-header {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
}
.modal-header span {
  color: #999;
  cursor: pointer;
}
.modal-header span.active {
  color: #ff3333;
  font-weight: bold;
}
.forget-link {
  font-size: 14px;
  color: #666;
  margin-left: auto;
}
.close-btn {
  position: absolute;
  top: 10px; right: 10px;
  background: none;
  border: none;
  font-size: 20px;
  color: #999;
  cursor: pointer;
}
.modal-body input {
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #eee;
  border-radius: 5px;
  box-sizing: border-box;
}
.modal-body button {
  width: 100%;
  padding: 10px;
  background: #ff3333;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
</style>
