<template>
  <div class="login-page">
    <div class="login-card">
      <!-- 左侧品牌区 -->
      <div class="brand-side">
        <div class="logo">🛒</div>
        <h1>商城后台管理系统</h1>
        <p class="slogan">商品管理 · 分类统计 · 库存监控</p>
        <ul class="feature-list">
          <li><span>✓</span> 商品新增、编辑、删除</li>
          <li><span>✓</span> 上下架状态与库存维护</li>
          <li><span>✓</span> 改动实时同步到前台商城</li>
        </ul>
      </div>

      <!-- 右侧表单区 -->
      <div class="form-side">
        <h2>管理员登录</h2>
        <p class="sub">请使用后台账号进入管理系统</p>

        <form @submit.prevent="handleLogin">
          <div class="field">
            <label for="admin-user">账号</label>
            <input
              id="admin-user"
              v-model.trim="form.username"
              type="text"
              autocomplete="username"
              placeholder="请输入管理员账号"
            />
          </div>

          <div class="field">
            <label for="admin-pwd">密码</label>
            <input
              id="admin-pwd"
              v-model="form.password"
              type="password"
              autocomplete="current-password"
              placeholder="请输入密码"
            />
          </div>

          <p class="error" v-if="error">{{ error }}</p>

          <button class="submit" type="submit" :disabled="loading">
            {{ loading ? '登录中…' : '登 录' }}
          </button>
        </form>

        <div class="hint">
          <div class="hint-title">演示账号</div>
          <div>账号 <b>admin</b> ｜ 密码 <b>admin123</b></div>
        </div>

        <!-- 前台是另一个应用，这里用普通链接跳出后台 -->
        <a href="/" class="back-link">← 返回前台商城</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { request } from '@/api/http'

const router = useRouter()

// 账号密码校验已交给后端（application.yml 的 shop.admin.*），前端不再硬编码
const form = reactive({ username: '', password: '' })
const error = ref('')
const loading = ref(false)

// 已登录则直接进后台，避免重复登录
onMounted(() => {
  if (localStorage.getItem('adminToken')) {
    router.replace('/home')
  }
})

const handleLogin = async () => {
  error.value = ''

  if (!form.username || !form.password) {
    error.value = '请填写账号和密码'
    return
  }

  loading.value = true

  try {
    // 真实请求后端：校验通过才返回令牌
    const res = await request('/auth/login', {
      method: 'POST',
      body: { username: form.username, password: form.password }
    })
    // 令牌存本地，之后所有写操作由 src/api/http.js 自动带在请求头里
    localStorage.setItem('adminToken', res.token)
    localStorage.setItem('adminUser', res.username)
    router.replace('/home')
  } catch (e) {
    error.value = e.message || '登录失败，请稍后重试'
    form.password = ''
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: linear-gradient(135deg, #1f2430 0%, #2c3444 60%, #3a4356 100%);
  box-sizing: border-box;
}

.login-card {
  display: flex;
  width: 100%;
  max-width: 780px;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
}

/* ===== 左侧品牌区 ===== */
.brand-side {
  width: 44%;
  padding: 40px 32px;
  background: linear-gradient(160deg, #ffd233 0%, #ffc107 100%);
  color: #3d3200;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.logo {
  font-size: 38px;
}
.brand-side h1 {
  margin: 14px 0 6px;
  font-size: 20px;
  line-height: 1.4;
}
.slogan {
  margin: 0 0 22px;
  font-size: 13px;
  color: #6b5a00;
}
.feature-list {
  margin: 0;
  padding: 0;
  list-style: none;
  font-size: 13px;
  line-height: 2;
}
.feature-list span {
  margin-right: 6px;
  font-weight: bold;
  color: #7a6400;
}

/* ===== 右侧表单区 ===== */
.form-side {
  flex: 1;
  padding: 40px 36px;
}
.form-side h2 {
  margin: 0 0 6px;
  font-size: 19px;
}
.sub {
  margin: 0 0 24px;
  font-size: 12px;
  color: #999;
}
.field {
  margin-bottom: 16px;
}
.field label {
  display: block;
  margin-bottom: 6px;
  font-size: 12px;
  color: #666;
}
.field input {
  width: 100%;
  height: 40px;
  padding: 0 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  box-sizing: border-box;
  transition: border-color 0.15s;
}
.field input:focus {
  border-color: #ffc107;
  box-shadow: 0 0 0 3px rgba(255, 193, 7, 0.15);
}
.error {
  margin: 0 0 12px;
  padding: 8px 10px;
  border-radius: 6px;
  background: #fff1f0;
  color: #d4380d;
  font-size: 12px;
}
.submit {
  width: 100%;
  height: 42px;
  border: none;
  border-radius: 8px;
  background: #ffc107;
  color: #3d3200;
  font-size: 15px;
  font-weight: bold;
  letter-spacing: 2px;
  cursor: pointer;
  transition: background 0.15s;
}
.submit:hover:not(:disabled) {
  background: #ffb300;
}
.submit:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
.hint {
  margin-top: 20px;
  padding: 10px 12px;
  border-radius: 8px;
  background: #f7f8fa;
  font-size: 12px;
  color: #666;
  line-height: 1.8;
}
.hint-title {
  font-weight: bold;
  color: #333;
}
.hint b {
  color: #d48806;
}
.back-link {
  display: inline-block;
  margin-top: 16px;
  font-size: 12px;
  color: #999;
  text-decoration: none;
}
.back-link:hover {
  color: #d48806;
}

@media (max-width: 700px) {
  .brand-side {
    display: none;
  }
  .form-side {
    padding: 32px 24px;
  }
}
</style>
