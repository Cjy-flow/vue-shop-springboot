<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="brand">
        <span class="brand-icon">🛒</span>
        <div class="brand-text">
          <div class="brand-name">商城后台</div>
          <div class="brand-sub">Admin Console</div>
        </div>
      </div>

      <nav class="nav">
        <router-link
          v-for="item in menus"
          :key="item.path"
          :to="item.path"
          class="nav-item"
        >
          <span class="nav-icon">{{ item.icon }}</span>
          <span class="nav-name">{{ item.name }}</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <router-link to="/home" class="side-link">← 返回前台商城</router-link>
        <button class="logout-btn" type="button" @click="logout">退出登录</button>
      </div>
    </aside>

    <!-- 主区域 -->
    <div class="main">
      <header class="topbar">
        <h1 class="page-title">{{ title }}</h1>
        <div class="topbar-right">
          <span class="admin-badge">admin</span>
          <span class="today">{{ today }}</span>
        </div>
      </header>
      <main class="content">
        <slot />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

defineProps({
  title: { type: String, default: '后台管理' }
})

const router = useRouter()

const menus = [
  { path: '/home', name: '数据概览', icon: '📊' },
  { path: '/goods', name: '商品管理', icon: '📦' },
  { path: '/category', name: '分类管理', icon: '🗂️' }
]

const today = computed(() =>
  new Date().toLocaleDateString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit'
  })
)

const logout = () => {
  localStorage.removeItem('adminToken')
  localStorage.removeItem('adminUser')
  router.replace('/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f6f8;
  font-size: 14px;
  color: #333;
}

/* ===== 侧边栏 ===== */
.sidebar {
  width: 210px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  background: #1f2430;
  color: #cfd3dc;
}
.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 18px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}
.brand-icon {
  font-size: 22px;
}
.brand-name {
  font-size: 15px;
  font-weight: bold;
  color: #fff;
}
.brand-sub {
  margin-top: 2px;
  font-size: 11px;
  color: #7d8494;
  letter-spacing: 0.4px;
}
.nav {
  flex: 1;
  padding: 12px 10px;
}
.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 11px 14px;
  margin-bottom: 4px;
  border-radius: 8px;
  color: #cfd3dc;
  text-decoration: none;
  font-size: 14px;
  transition: background 0.15s, color 0.15s;
}
.nav-item:hover {
  background: rgba(255, 255, 255, 0.07);
  color: #fff;
}
/* 当前路由高亮 */
.nav-item.router-link-active {
  background: #ffd233;
  color: #1f2430;
  font-weight: bold;
}
.nav-icon {
  font-size: 15px;
}
.sidebar-footer {
  padding: 14px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}
.side-link {
  display: block;
  margin-bottom: 10px;
  color: #8f96a5;
  font-size: 12px;
  text-decoration: none;
}
.side-link:hover {
  color: #ffd233;
}
.logout-btn {
  width: 100%;
  height: 32px;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 6px;
  background: transparent;
  color: #cfd3dc;
  font-size: 12px;
  cursor: pointer;
}
.logout-btn:hover {
  border-color: #ff6b6b;
  color: #ff8a8a;
}

/* ===== 主区域 ===== */
.main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid #eceef2;
}
.page-title {
  margin: 0;
  font-size: 17px;
  font-weight: bold;
}
.topbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: #888;
}
.admin-badge {
  padding: 3px 10px;
  border-radius: 12px;
  background: #fff7d6;
  color: #8a6d00;
  font-weight: bold;
}
.content {
  flex: 1;
  padding: 20px 24px 32px;
  overflow-x: hidden;
}

/* 窄屏：侧边栏只留图标 */
@media (max-width: 820px) {
  .sidebar {
    width: 64px;
  }
  .brand-text,
  .nav-name,
  .side-link,
  .logout-btn {
    display: none;
  }
  .brand {
    justify-content: center;
    padding: 20px 8px;
  }
  .nav-item {
    justify-content: center;
    padding: 12px 6px;
  }
  .sidebar-footer {
    padding: 8px;
  }
  .content {
    padding: 14px 12px 24px;
  }
  .topbar {
    padding: 0 14px;
  }
  .today {
    display: none;
  }
}
</style>
