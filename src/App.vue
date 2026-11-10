<template>
  <div id="app">
    <router-view />

    <!-- 自定义底部导航栏 -->
    <div class="custom-tabbar">
      <div class="tab-item" 
           v-for="tab in tabs" 
           :key="tab.path"
           @click="goTo(tab.path)"
           :class="{ active: $route.path === tab.path }">
        
        <div class="tab-icon-wrap" v-if="tab.path === '/message'">
          <img 
            :src="$route.path === tab.path ? tab.activeIcon : tab.icon" 
            alt="" 
            class="tab-icon"
          >
          <span class="badge" v-if="unreadCount > 0">{{ unreadCount }}</span>
        </div>

        <div class="tab-icon-wrap" v-else>
          <img 
            :src="$route.path === tab.path ? tab.activeIcon : tab.icon" 
            alt="" 
            class="tab-icon"
          >
        </div>

        <span class="tab-text">{{ tab.name }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { computed } from 'vue'
import { useStore } from 'vuex'

import homeIcon from './assets/icons/home.svg'
import categoryIcon from './assets/icons/category.svg'
import messageIcon from './assets/icons/message.svg'
import cartIcon from './assets/icons/cart.svg'
import mineIcon from './assets/icons/mine.svg'

import homeActive from './assets/icons/home-active.svg'
import categoryActive from './assets/icons/category-active.svg'
import messageActive from './assets/icons/message-active.svg'
import cartActive from './assets/icons/cart-active.svg'
import mineActive from './assets/icons/mine-active.svg'

const router = useRouter()
const store = useStore()

const unreadCount = computed(() => store.getters.unreadCount)

// 后台已拆成独立应用（/admin/ 另开一个页面），这里不再需要判断后台路由

const tabs = [
  { path: '/home', name: '首页', icon: homeIcon, activeIcon: homeActive },
  { path: '/category', name: '分类', icon: categoryIcon, activeIcon: categoryActive },
  { path: '/message', name: '消息', icon: messageIcon, activeIcon: messageActive },
  { path: '/cart', name: '购物车', icon: cartIcon, activeIcon: cartActive },
  { path: '/mine', name: '我的', icon: mineIcon, activeIcon: mineActive }
]

const goTo = (path) => {
  router.push(path)
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  width: 100%;
  max-width: 750px;
  margin: 0 auto;
  min-height: 100vh;
  padding-bottom: 50px !important;
}

/* 🔥 底部导航 收缩到和灰色页面完全对齐 */
.custom-tabbar {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 750px; /* 与页面灰色区域完全对齐 */
  height: 50px;
  background: #fff;
  display: flex;
  justify-content: space-around;
  align-items: center;
  box-shadow: 0 -1px 5px rgba(0,0,0,0.1);
  z-index: 999;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #666;
  transition: color 0.2s;
}

.tab-item.active {
  color: #ee0a24;
}

.tab-icon-wrap {
  position: relative;
  width: 24px;
  height: 24px;
  margin-bottom: 2px;
}
.tab-icon {
  width: 100%;
  height: 100%;
}

.badge {
  position: absolute;
  top: -4px;
  right: -6px;
  background: #ff3333;
  color: #fff;
  font-size: 11px;
  padding: 1px 4px;
  border-radius: 10px;
  min-width: 16px;
  text-align: center;
  box-sizing: border-box;
  line-height: 1.2;
}

.tab-text {
  font-size: 12px;
}

.goods-item {
  width: 48%;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  transition: transform 0.2s;
}

.goods-item:hover {
  transform: translateY(-2px);
}
</style>